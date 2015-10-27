package edu.umd.lib.hippo.update;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.VersionManager;

import org.onehippo.cms7.repository.migration.BasicUpdater;

/**
 * This program provides the specific exhibit related property changes needed to
 * alter the exhibit namespace to meet requirements for 2015-09-25. Specifically
 * we need to remove exhibit:basedocument from exhibit:bodyblock.
 *
 * @author phammer
 *
 */
public class ExhibitUpdater extends BasicUpdater {

  @Override
  public void update(final Node node) throws RepositoryException {
    final String primaryNodeTypeName = node.getPrimaryNodeType().getName();
    if (primaryNodeTypeName.equals(getOldNamespacePrefix() + ":bodyblock")) {

      Session session = node.getSession();
      VersionManager versionManager = session.getWorkspace()
          .getVersionManager();
      if (!node.isCheckedOut()) {
        versionManager.checkout(node.getParent().getPath());
      }

      /*
       * Relaxed nodes may not be unstructured So, if it is not relaxed, then
       * make it unstructured
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
       * The hippo:related property will be illegal after the migration so
       * remove it, if it exists.
       */
      if (node.hasProperty("hippo:related")) {
        node.getProperty("hippo:related").remove();
      }

      /*
       * Set the primary type to the new cnd being imported
       */
      node.setPrimaryType(getNewNamespacePrefix() + ":bodyblock");

      /*
       * If we added the unstructured mixin earlier, remove it now.
       */
      if (notRelaxed) {
        node.removeMixin("hipposys:unstructured");
      }

    }
    super.update(node);
  }

  public static void main(String[] args) {
    ExhibitUpdater updater = new ExhibitUpdater();
    updater.main(args);
  }

}
