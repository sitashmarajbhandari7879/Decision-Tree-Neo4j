package gainratio;

import definition.Attribute;
import definition.Instance;
import node.TreeNode;
import core.ConstructTree;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ConstructTreeGR extends ConstructTree{
	
	
	public ConstructTreeGR(ArrayList<Instance> instances, ArrayList<Attribute> attributes, Attribute target) {
		super(instances, attributes, target);
	}

	/**
	 * Construct tree
	 * 
	 * @return TreeNode
	 * @throws IOException
	 */
	public TreeNode construct() throws IOException {
		return constructTree(target, attributes, instances);
	}

	/**
	 * Construct tree recursively. First make the root node, then construct its
	 * subtrees recursively, and finally connect root with subtrees.
	 * 
	 * @param target
	 * @param attributes
	 * @param instances
	 * @return TreeNode
	 * @throws IOException
	 */
	private TreeNode constructTree(Attribute target, ArrayList<Attribute> attributes, ArrayList<Instance> instances)
			throws IOException {

		/*
		 * Stop when (1) entropy is zero (2) no attribute left
		 */
		if (EntropyGR.calculate(target, instances) == 0 || attributes.size() == 0) {
			String leafLabel = "";
			if (EntropyGR.calculate(target, instances) == 0) {
				leafLabel = instances.get(0).getAttributeValuePairs().get(target.getName());
			} else {
				leafLabel = getMajorityLabel(target, instances);
			}
			TreeNode leaf = new TreeNode(leafLabel);
			return leaf;
		}

		// Choose the root attribute
		ChooseAttributeGR choose = new ChooseAttributeGR(target, attributes, instances);
		Attribute rootAttr = choose.getChosen();

		// Remove the chosen attribute from attribute set
		attributes.remove(rootAttr);

		// Make a new root
		TreeNode root = new TreeNode(rootAttr);

		// Get value subsets of the root attribute to construct branches
		HashMap<String, ArrayList<Instance>> valueSubsets = choose.getSubset();

		if (valueSubsets == null || valueSubsets.size() == 0) {
			String leafLabel = getMajorityLabel(target, instances);
			TreeNode leaf = new TreeNode(leafLabel);
			return leaf;

		} else {
			for (String valueName : valueSubsets.keySet()) {
				ArrayList<Instance> subset = valueSubsets.get(valueName);
				if (subset.size() == 0) {
					String leafLabel = getMajorityLabel(target, instances);
					TreeNode leaf = new TreeNode(leafLabel);
					root.addChild(valueName, leaf);
				} else {
					TreeNode child = constructTree(target, attributes, subset);
					root.addChild(valueName, child);
				}
			}
		}

		attributes.add(rootAttr);

		return root;
	}
}