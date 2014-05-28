package parser.utils.tree;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * 这是一个例子
 */
public class TreeHelper {

    private TreeNode root;
    private List<TreeNode> tempNodeList;
    private boolean isValidTree = true;

    public TreeHelper() {
    }

    public TreeHelper(List<TreeNode> treeNodeList) {
        tempNodeList = treeNodeList;
        generateTree();
    }

    public static TreeNode getTreeNodeById(TreeNode tree, int id) {
        if (tree == null)
            return null;
        TreeNode treeNode = tree.findTreeNodeById(id);
        return treeNode;
    }

    /**
     * adapt the entities to the corresponding treeNode
     *
     * @param entityList list that contains the entities
     * @return the list containg the corresponding treeNodes of the entities
     */
    public static List<TreeNode> changeEnititiesToTreeNodes(List entityList) {
        OrganizationEntity orgEntity = new OrganizationEntity();
        List<TreeNode> tempNodeList = new ArrayList<TreeNode>();
        TreeNode treeNode;

        Iterator it = entityList.iterator();
        while (it.hasNext()) {
            orgEntity = (OrganizationEntity) it.next();
            treeNode = new TreeNode();
            treeNode.setObj(orgEntity);
            treeNode.setParentId(orgEntity.getParentId());
            treeNode.setSelfId(orgEntity.getOrgId());
            treeNode.setNodeName(orgEntity.getOrgName());
            tempNodeList.add(treeNode);
        }
        return tempNodeList;
    }

    /**
     * generate a tree from the given treeNode or entity list
     */
    public void generateTree() {
        HashMap nodeMap = putNodesIntoMap();
        putChildIntoParent(nodeMap);
    }

    /**
     * put all the treeNodes into a hash table by its id as the key
     *
     * @return hashmap that contains the treenodes
     */
    protected HashMap putNodesIntoMap() {
        int maxId = Integer.MAX_VALUE;
        HashMap nodeMap = new HashMap<String, TreeNode>();
        Iterator it = tempNodeList.iterator();
        while (it.hasNext()) {
            TreeNode treeNode = (TreeNode) it.next();
            int id = treeNode.getSelfId();
            if (id < maxId) {
                maxId = id;
                this.root = treeNode;
            }
            String keyId = String.valueOf(id);

            nodeMap.put(keyId, treeNode);
            // System.out.println("keyId: " +keyId);
        }
        return nodeMap;
    }

    /**
     * set the parent nodes point to the child nodes
     *
     * @param nodeMap a hashmap that contains all the treenodes by its id as the key
     */
    protected void putChildIntoParent(HashMap nodeMap) {
        Iterator it = nodeMap.values().iterator();
        while (it.hasNext()) {
            TreeNode treeNode = (TreeNode) it.next();
            int parentId = treeNode.getParentId();
            String parentKeyId = String.valueOf(parentId);
            if (nodeMap.containsKey(parentKeyId)) {
                TreeNode parentNode = (TreeNode) nodeMap.get(parentKeyId);
                if (parentNode == null) {
                    this.isValidTree = false;
                    return;
                } else {
                    parentNode.addChildNode(treeNode);
                    // System.out.println("childId: " +treeNode.getSelfId()+" parentId: "+parentNode.getSelfId());
                }
            }
        }
    }

    /**
     * initialize the tempNodeList property
     */
    protected void initTempNodeList() {
        if (this.tempNodeList == null) {
            this.tempNodeList = new ArrayList<TreeNode>();
        }
    }

    /**
     * add a tree node to the tempNodeList
     */
    public void addTreeNode(TreeNode treeNode) {
        initTempNodeList();
        this.tempNodeList.add(treeNode);
    }

    /**
     * insert a tree node to the tree generated already
     *
     * @return show the insert operation is ok or not
     */
    public boolean insertTreeNode(TreeNode treeNode) {
        boolean insertFlag = root.insertJuniorNode(treeNode);
        return insertFlag;
    }

    public boolean isValidTree() {
        return this.isValidTree;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public List<TreeNode> getTempNodeList() {
        return tempNodeList;
    }

    public void setTempNodeList(List<TreeNode> tempNodeList) {
        this.tempNodeList = tempNodeList;
    }

}