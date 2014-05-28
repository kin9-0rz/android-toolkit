package tree;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode implements Serializable {
    /**
     * 节点名
     */
    protected String nodeName;
    /**
     * 存放的对象
     */
    protected Object obj;
    /**
     * 父节点
     */
    protected TreeNode parentNode;
    /**
     * 孩子节点列表
     */
    protected List<TreeNode> childList;
    /**
     * 父节点ID
     */
    private int parentId;
    /**
     * 自己的ID
     */
    private int selfId;

    public TreeNode() {
        initChildList();
    }

    public TreeNode(TreeNode parentNode) {
        this.getParentNode();
        initChildList();
    }

    /**
     * 是否叶子节点
     * @return true 叶子节点；false 则非叶子节点.
     */
    public boolean isLeaf() {
        if (childList == null) {
            return true;
        } else {
            if (childList.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }


    /**
     * 插入一个孩子节点到当前节点中
     * @param treeNode 孩子节点
     */
    public void addChildNode(TreeNode treeNode) {
        initChildList();
        childList.add(treeNode);
    }

    /**
     * 初始化孩子节点列表
     */
    public void initChildList() {
        if (childList == null) {
            childList = new ArrayList<>();
        }
    }

    /**
     * 是否有效树  [暂时没用]
     * @return true 是
     */
    public boolean isValidTree() {
        return true;
    }


    /**
     * 返回当前节点的所有父辈节点集合
     * @return 所有父辈节点的列表
     */
    public List<TreeNode> getElders() {
        List<TreeNode> elderList = new ArrayList<>();
        TreeNode parentNode = this.getParentNode();
        if (parentNode == null) {
            return elderList;
        } else {
            elderList.add(parentNode);
            elderList.addAll(parentNode.getElders());
            return elderList;
        }
    }


    /**
     * 返回当前节点的晚辈集合
     * @return 节点列表
     */
    public List<TreeNode> getJuniors() {
        List<TreeNode> juniorList = new ArrayList<>();
        List<TreeNode> childList = this.getChildList();
        if (childList == null) {
            return juniorList;
        } else {
//            int childNumber = childList.size();
//            for (int i = 0; i < childNumber; i++) {
//                TreeNode junior = childList.get(i);
//                juniorList.add(junior);
//                juniorList.addAll(junior.getJuniors());
//            }

            for (TreeNode junior : childList) {
                juniorList.add(junior);
                juniorList.addAll(junior.getJuniors());
            }

            return juniorList;
        }
    }

    /**
     * 返回当前节点的孩子集合
     * @return 孩子节点列表
     */
    public List<TreeNode> getChildList() {
        return childList;
    }

    /**
     * 设置孩子节点类别
     * @param childList 孩子节点列表
     */
    public void setChildList(List<TreeNode> childList) {
        this.childList = childList;
    }

    /**
     * 删除节点和它下面的晚辈
     */
    public void deleteNode() {
        TreeNode parentNode = this.getParentNode();
        int id = this.getSelfId();

        if (parentNode != null) {
            parentNode.deleteChildNode(id);
        }
    }

    /**
     * 删除当前节点的某个子节点
     * @param childId
     */
    public void deleteChildNode(int childId) {
        List<TreeNode> childList = this.getChildList();
        int childNumber = childList.size();
        for (int i = 0; i < childNumber; i++) {
            TreeNode child = childList.get(i);
            if (child.getSelfId() == childId) {
                childList.remove(i);
                return;
            }
        }
    }

    /**
     * 动态的插入一个新的节点到当前树中
     * @param treeNode 节点
     * @return true 插入成功; false 失败.
     */
    public boolean insertJuniorNode(TreeNode treeNode) {
        int juniorParentId = treeNode.getParentId();
        if (this.parentId == juniorParentId) {
            addChildNode(treeNode);
            return true;
        } else {
            List<TreeNode> childList = this.getChildList();

            boolean insertFlag;

//            int childNumber = childList.size();
//            for (int i = 0; i < childNumber; i++) {
//                TreeNode childNode = childList.get(i);
//                insertFlag = childNode.insertJuniorNode(treeNode);
//                if (insertFlag == true)
//                    return true;
//            }

            for (TreeNode childNode : childList) {
                insertFlag = childNode.insertJuniorNode(treeNode);
                if (insertFlag)
                    return true;
            }

            return false;
        }
    }


    /**
     * 找到一颗树中某个节点
     * @param id 节点id
     * @return 节点
     */
    public TreeNode findTreeNodeById(int id) {
        if (this.selfId == id)
            return this;
        if (childList.isEmpty() || childList == null) {
            return null;
        } else {
//            int childNumber = childList.size();
//            for (int i = 0; i < childNumber; i++) {
//                TreeNode child = childList.get(i);
//                TreeNode resultNode = child.findTreeNodeById(id);
//                if (resultNode != null) {
//                    return resultNode;
//                }
//            }

            for (TreeNode child : childList) {
                TreeNode resultNode = child.findTreeNodeById(id);
                if (resultNode != null) {
                    return resultNode;
                }
            }

            return null;
        }
    }

    /**
     * 遍历一棵树，层次遍历
     */
    public void traverse() {
        if (selfId < 0)
            return;
        print(this.selfId);

        if (childList == null || childList.isEmpty()) {
            return;
        }

//        int childNumber = childList.size();
//        for (int i = 0; i < childNumber; i++) {
//            TreeNode child = childList.get(i);
//            child.traverse();
//        }

        for (TreeNode child : childList) {
            child.traverse();
        }

    }

    /**
     * 打印内容
     * @param content 内容
     */
    public void print(String content) {
        System.out.println(content);
    }

    /**
     * 打印内容
     * @param content 内容
     */
    public void print(int content) {
        System.out.println(String.valueOf(content));
    }

    /**
     * 获得父节点ID
     * @return id
     */
    public int getParentId() {
        return parentId;
    }

    /**
     * 设置父节点ID
     * @param parentId 父节点ID
     */
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    /**
     * 获得当前节点ID
     * @return ID
     */
    public int getSelfId() {
        return selfId;
    }

    /**
     * 设置当前节点ID
     * @param selfId ID
     */
    public void setSelfId(int selfId) {
        this.selfId = selfId;
    }

    /**
     * 获得父节点
     * @return 父节点
     */
    public TreeNode getParentNode() {
        return parentNode;
    }

    /**
     * 设置父节点
     * @param parentNode 父节点
     */
    public void setParentNode(TreeNode parentNode) {
        this.parentNode = parentNode;
    }

    /**
     * 获得节点名
     * @return 节点名
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * 设置节点名
     * @param nodeName 节点名
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * 获得存储对象
     * @return
     */
    public Object getObj() {
        return obj;
    }

    /**
     * 设置存储对象
     * @param obj 对象
     */
    public void setObj(Object obj) {
        this.obj = obj;
    }
}
