/*
 * Copyright 2008 Android4ME
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package parser.axml;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParser;
import parser.arsc.ARSCParser;
import parser.axml.res.AXMLParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 解析 AndroidManifest.xml 文件。
 */
public class ManifestParser {
    private static final float RADIX_MULTS[] = {
            0.00390625F, 3.051758E-005F, 1.192093E-007F, 4.656613E-010F
    };

    private static final String DIMENSION_UNITS[] = {
            "px", "dip", "sp", "pt", "in", "mm", "", ""
    };

    private static final String FRACTION_UNITS[] = {
            "%", "%p", "", "", "", "", "", ""
    };

    private static final int
            TYPE_REFERENCE = 1,
            TYPE_ATTRIBUTE = 2,
            TYPE_STRING = 3,
            TYPE_FLOAT = 4,
            TYPE_DIMENSION = 5,
            TYPE_FRACTION = 6,
            TYPE_FIRST_INT = 16,
            TYPE_INT_HEX = 17,
            TYPE_INT_BOOLEAN = 18,
            TYPE_FIRST_COLOR_INT = 28,
            TYPE_LAST_COLOR_INT = 31,
            TYPE_LAST_INT = 31,
            COMPLEX_UNIT_MASK = 15;

    private byte[] m_arsc;

    private StringBuilder m_xml;
    /**
     * 临时增加的值,假为后台运行
     */
    private boolean m_noback = false;

    /**
     * 获取命名空间的前缀
     *
     * @param prefix 字符串
     * @return 命名空间（属性前缀）
     */
    private static String getNamespacePrefix(String prefix) {
        if (prefix == null || prefix.length() == 0) {
            return "";
        }

        if (prefix.contains("http://schemas.android.com/apk/res/android")) {
            return "android:";
        }

        return prefix + ":";
    }

    /**
     * 这个也是获取前缀
     *
     * @param id id
     * @return 前缀
     */
    private static String getPackage(int id) {
        if (id >>> 24 == 1) {
            return "android:";
        }
        return "";
    }


    /////////////////////////////////// ILLEGAL STUFF, DON'T LOOK :)

    public static float complexToFloat(int complex) {
        return (float) (complex & 0xFFFFFF00) * RADIX_MULTS[(complex >> 4) & 3];
    }

    /**
     * 解析 AndroidManifest.xml 文件，并返回 <code>ManifestInfo </code>.
     *
     * @param pFile apk 文件
     * @return 解析成功，则返回清单信息；如果解析出错，或不存在清单文件，则返回 null。
     */
    public ManifestInfo parse(File pFile) throws IOException {
        ManifestInfo manifestInfo = new ManifestInfo();
        m_noback = false;

        ZipFile zipFile;
        InputStream aXMLInputStream = null;
        InputStream arscInputStream = null;
        try {
            zipFile = new ZipFile(pFile);
            ZipEntry zipEntry = zipFile.getEntry("AndroidManifest.xml");

            if (zipEntry != null) {
                aXMLInputStream = zipFile.getInputStream(zipEntry);
            } else {
                manifestInfo = null;
            }

            zipEntry = zipFile.getEntry("resources.arsc");
            if (zipEntry != null) {
                arscInputStream = zipFile.getInputStream(zipEntry);
            }

            if (arscInputStream != null) {
                m_arsc = IOUtils.toByteArray(arscInputStream);
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

        if (aXMLInputStream != null) {
            try {
                parseManifest(aXMLInputStream, manifestInfo);
                manifestInfo.back = !m_noback;
                manifestInfo.xml = (m_xml == null) ? null : m_xml.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        try {
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (aXMLInputStream != null) {
            try {
                aXMLInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (arscInputStream != null) {
            try {
                arscInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return manifestInfo;
    }

    public ManifestInfo parse(InputStream amInputStream, byte[] arscBytes)
            throws IOException {
        ManifestInfo manifestInfo = new ManifestInfo();
        m_noback = false;
        m_arsc = arscBytes;

        if (amInputStream != null) {
            try {
                parseManifest(amInputStream, manifestInfo);
                manifestInfo.back = !m_noback;
                manifestInfo.xml = (m_xml == null) ? null : m_xml.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return manifestInfo;
    }


    /**
     * 解析 AndroidManifest.xml
     *
     * @param amInputStream AndroidManifest.xml 输入流
     * @param manifestInfo  ManifestInfo
     * @throws IOException
     */
    private void parseManifest(InputStream amInputStream, ManifestInfo manifestInfo)
            throws IOException {

        final HashSet<String> permissionsSet = new HashSet<>();
        final HashMap<String, ArrayList<String>> activities = new HashMap<>();
        final ArrayList<String> services = new ArrayList<>();
        final HashMap<String, ArrayList<String>> receivers = new HashMap<>();
        String key = null;
        final byte FLAG_ACTIVITY = 0;
        final byte FLAG_RECEIVER = 1;
        byte flag = 2;

        final AXMLParser parser = new AXMLParser(amInputStream);
        final String indentStep = "\t";
        StringBuilder indent = new StringBuilder(10);
        m_xml = new StringBuilder(100);
        while (true) {
            final int type = parser.next();
            if (type == XmlPullParser.END_DOCUMENT) {
                break;
            }

            final String tagName = parser.getName();
            if (type == XmlPullParser.END_TAG && tagName.equals("manifest")) {
                break;
            }

            switch (type) {
                case XmlPullParser.START_DOCUMENT:
                    m_xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
                    break;
                case AXMLParser.CHUNK_XML_START_TAG:
//                    final String tagName = parser.getName();
                    m_xml.append(indent).append("<").append(tagName);
                    indent.append(indentStep);

                    switch (tagName) {
                        case "manifest":
                            parseTagManifest(parser, manifestInfo);
                            break;
                        case "application":
                            parseTagApplciation(parser, manifestInfo);
                            break;
                        case "uses-permission":
                            parseTagPermission(parser, permissionsSet);
                            break;
                        case "activity":
                        case "activity-alias":
                            key = parseTagActivity(parser);
                            activities.put(key, new ArrayList<String>());
                            flag = FLAG_ACTIVITY;
                            break;
                        case "service":
                            parseTagService(parser, services);
                            break;
                        case "receiver":
                            key = parseTagReceiver(parser);
                            receivers.put(key, new ArrayList<String>());
                            flag = FLAG_RECEIVER;
                            break;
                        case "action":
                            if (flag == FLAG_ACTIVITY) {
                                parseTagAction(parser, activities.get(key));
                            } else if (flag == FLAG_RECEIVER) {
                                parseTagAction(parser, receivers.get(key));
                            }
                            break;
                        case "category":
                            if (flag == FLAG_ACTIVITY) {
                                parseTagCategory(parser, activities.get(key));
                            } else if (flag == FLAG_RECEIVER) {
                                parseTagCategory(parser, receivers.get(key));
                            }
                            break;
                    }

                    //将一个标签的所有属性值写入xml。
                    for (int i = 0; i != parser.getAttributeCount(); ++i) {
                        final String v = makeAttributeValue(parser, i);
                        final String attr = getNamespacePrefix(parser
                                .getAttributePrefix(i))
                                + parser.getAttributeName(i);
                        //如果有界面，非后台运行
                        if (v.equals("android.intent.category.LAUNCHER"))
                            m_noback = true;

                        m_xml.append(" ").append(attr).append("=\"").append(v).append("\"");
                    }
                    m_xml.append(">\n");
                    break;
                case AXMLParser.CHUNK_XML_END_TAG:
                    indent.setLength(indent.length() - indentStep.length());
                    m_xml.append(indent).append("</").append(parser.getName()).append(">\n");
                    break;
                case AXMLParser.CHUNK_XML_TEXT:
                    break;
            }
        }

        ArrayList<String> requestedPermissions = new ArrayList<>(permissionsSet);
        Collections.sort(requestedPermissions);
        manifestInfo.requestedPermissions = requestedPermissions;
        manifestInfo.services = services;
        manifestInfo.receivers = receivers;
        manifestInfo.activities = activities;
    }

    private String parseTagActivity(AXMLParser parser) {
        StringBuilder value = new StringBuilder();
        for (int i = 0; i != parser.getAttributeCount(); ++i) {
            String v = makeAttributeValue(parser, i);
            final String attr = getNamespacePrefix(parser
                    .getAttributeNamespace(i)) + parser.getAttributeName(i);
            if ("android:name".equals(attr)) {
                return v;
            } else if ("".equals(attr)) {
                value.append(v).append("-");
            }
        }

        return value.append(" [NOT Default AXML!]").toString();
    }

    /**
     * 解析 manifest 标签属性值，获取 versionName, versionCode, packageName
     */
    private void parseTagManifest(AXMLParser parser, ManifestInfo manifestInfo) {
        m_xml.append(" xmlns:android=\"http://schemas.android.com/apk/res/android\"");
        for (int i = 0; i != parser.getAttributeCount(); ++i) {
            final String attributeValue = makeAttributeValue(parser, i);
            final String attribute = getNamespacePrefix(parser.getAttributePrefix(i)) + parser.getAttributeName(i);
            if ("android:versionName".equals(attribute)) {
                manifestInfo.versionName = attributeValue;
            } else if ("package".equals(attribute)) {
                manifestInfo.packageName = attributeValue;
            } else if ("android:versionCode".equals(attribute)) {
                manifestInfo.versionCode = attributeValue;
            }
        }
    }

    /**
     * 解析applciation标签属性值，获取label(appname)
     */
    private void parseTagApplciation(AXMLParser parser, ManifestInfo pkgInfo) {
        for (int i = 0; i != parser.getAttributeCount(); ++i) {
            final String v = makeAttributeValue(parser, i);
            final String attr = getNamespacePrefix(parser.getAttributePrefix(i)) + parser.getAttributeName(i);
            if ("android:label".equals(attr)) {
                pkgInfo.label = v;
                break;
            }
        }
    }

    /**
     * 获取权限
     */
    private void parseTagPermission(AXMLParser parser, Collection<String> requestedPermissions) {
        for (int i = 0; i != parser.getAttributeCount(); ++i) {
            String attributeName = parser.getAttributeName(i);
            if (attributeName.contains("name")) {
                String v = makeAttributeValue(parser, i);
                if (v.startsWith("android.permission.")) {
                    v = v.substring(19);
                }
                requestedPermissions.add(v);
            } else if (attributeName.equals("")) {
                String v = makeAttributeValue(parser, i);
                if (v.startsWith("android.permission.")) {
                    v = v.substring(19);
                }
                requestedPermissions.add(v);
            }
        }

    }

    /**
     * 解析 service 标签
     *
     * @param parser
     * @param services
     */
    private void parseTagService(AXMLParser parser, ArrayList<String> services) {
        StringBuilder nullValue = new StringBuilder();
        for (int i = 0; i != parser.getAttributeCount(); ++i) {
            String value = makeAttributeValue(parser, i);
            final String attr = getNamespacePrefix(parser
                    .getAttributeNamespace(i)) + parser.getAttributeName(i);
            if ("android:name".equals(attr)) {
                // <service android:name="">
                services.add(value);
            } else if ("".equals(attr)) {
                // <service ="service_name">
                nullValue.append(value).append(" - ");
                services.add(nullValue.append(" [NOT Default AXML!]").toString());
            }
        }


//        services.add(nullValue.append(" [NOT Default AXML!]").toString());
    }

    /**
     * 解析 reveiver 标签
     *
     * @param parser
     */
    private String parseTagReceiver(AXMLParser parser) {
        StringBuilder nullValue = new StringBuilder();
        for (int i = 0; i != parser.getAttributeCount(); ++i) {
            String v = makeAttributeValue(parser, i);
            final String attr = getNamespacePrefix(parser
                    .getAttributeNamespace(i)) + parser.getAttributeName(i);
            if ("android:name".equals(attr)) {
                return v;
            } else if ("".equals(attr)) {
                nullValue.append(v).append(" - ");
            }
        }

        return nullValue.append("[NOT Default AXML!]").toString();
    }

    private void parseTagAction(AXMLParser parser, ArrayList<String> act) {
        for (int i = 0; i != parser.getAttributeCount(); ++i) {
            String v = makeAttributeValue(parser, i);
            final String attr = getNamespacePrefix(parser
                    .getAttributeNamespace(i)) + parser.getAttributeName(i);
            if ("android:name".equals(attr)) {
                act.add(v);
            }
        }
    }

    private void parseTagCategory(AXMLParser parser, ArrayList<String> intent) {
        for (int i = 0; i != parser.getAttributeCount(); ++i) {
            final String v = makeAttributeValue(parser, i);
            final String attr = getNamespacePrefix(parser
                    .getAttributeNamespace(i)) + parser.getAttributeName(i);
            if ("android:name".equals(attr)) {
                intent.add(v);
            }
        }
    }

    //获取属性值
    private String makeAttributeValue(AXMLParser parser, int index) {
        final int type = parser.getAttributeValueType(index);
        final int data = parser.getAttributeValue(index);

        if (type == TYPE_STRING) {
            return parser.getAttributeValueString(index);
        }
        if (type == TYPE_ATTRIBUTE) {
            return String.format("?%s%08X", getPackage(data), data);
        }
        if (type == TYPE_REFERENCE) {
            if (data >>> 24 == 1 || m_arsc == null)
                return String.format("@%s%08X", getPackage(data), data);

            String name = new ARSCParser().parser(m_arsc, data);
            if (name != null)
                return name;
            else
                return String.format("@%08X", data);
        }
        if (type == TYPE_FLOAT) {
            return String.valueOf(Float.intBitsToFloat(data));
        }
        if (type == TYPE_INT_HEX) {
            return String.format("0x%08X", data);
        }
        if (type == TYPE_INT_BOOLEAN) {
            return data != 0 ? "true" : "false";
        }
        if (type == TYPE_DIMENSION) {
            return Float.toString(complexToFloat(data))
                    + DIMENSION_UNITS[data & COMPLEX_UNIT_MASK];
        }
        if (type == TYPE_FRACTION) {
            return Float.toString(complexToFloat(data))
                    + FRACTION_UNITS[data & COMPLEX_UNIT_MASK];
        }
        if (type >= TYPE_FIRST_COLOR_INT && type <= TYPE_LAST_COLOR_INT) {
            return String.format("#%08X", data);
        }
        if (type >= TYPE_FIRST_INT && type <= TYPE_LAST_INT) {
            return String.valueOf(data);
        }
        return String.format("<0x%X, type 0x%02X>", data, type);

    }

}
