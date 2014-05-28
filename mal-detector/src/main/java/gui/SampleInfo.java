package gui;

public class SampleInfo {
    public String fileSHA256;
    public String fileName;
    public String varName;
    public String pkgName;
    public String label;
    public String cert;
    public String permissions;
    public String receivers;
    public String services;

    @Override
    public String toString() {
        return "SampleInfo{" +
                "fileSHA256='" + fileSHA256 + '\'' +
                ", fileName='" + fileName + '\'' +
                ", var_name='" + varName + '\'' +
                ", pkgName='" + pkgName + '\'' +
                ", label='" + label + '\'' +
                ", cert='" + cert + '\'' +
                ", permissions='" + permissions + '\'' +
                ", receivers='" + receivers + '\'' +
                ", services='" + services + '\'' +
                '}';
    }
}
