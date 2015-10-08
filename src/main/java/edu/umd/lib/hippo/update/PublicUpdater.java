package edu.umd.lib.hippo.update;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.onehippo.cms7.repository.migration.BasicUpdater;

/**
 * This program provides the specific exhibit related property changes needed to
 * alter the public namespace to meet requirements for 2015-09-25. Specifically
 * we need to remove exhibit:basedocument from public:bodyblock.
 *
 * @author phammer
 *
 */
public class PublicUpdater extends BasicUpdater {

	@Override
	public void update(final Node node) throws RepositoryException {
		final String primaryNodeTypeName = node.getPrimaryNodeType().getName();
		if (primaryNodeTypeName.equals(getOldNamespacePrefix()
				+ ":htmlfragmentblock")) {

			/*
			 * Relaxed nodes may not be unstructured So, if it is not relaxed,
			 * then make it unstructured
			 */
			boolean notRelaxed = false;
			if (node.isNodeType("hippostd:relaxed")) {
				System.out.println("Has Relaxed");

			} else {
				System.out.println("Not Relaxed");
				notRelaxed = true;
				node.addMixin("hipposys:unstructured");
			}

			/*
			 * Fields that will be illegal after the migration have to be
			 * removed at this point, if they exist.
			 */
			if (node.hasProperty("hippo:related")) {
				node.getProperty("hippo:related").remove();
			}

			/*
			 * Set the primary type to the new cnd being imported
			 */
			node.setPrimaryType(getNewNamespacePrefix() + ":htmlfragmentblock");

			/*
			 * If we added the unstructured mixin earlier, remove it now.
			 */
			if (notRelaxed) {
				node.removeMixin("hipposys:unstructured");
			}

		}

		if (primaryNodeTypeName.equals(getOldNamespacePrefix() + ":bookmark")) {

			// Add any missing properties
			if (!node.hasProperty(getNewNamespacePrefix() + ":username")) {
				node.setProperty(getNewNamespacePrefix() + ":username",
						"phammer");
			}

		}
		super.update(node);
	}

	public static void main(String[] args) {
		PublicUpdater updater = new PublicUpdater();
		updater.main(args);
	}

}
