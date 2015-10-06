## Namespace Migrator

There are a number of cases where upgrading Hippo requires a change to one or more namespace cnd files.  If simply importing the files succeeds, then all is fine but there are many cases where the desired change is too large or too fundamental and the simple import of the namespace change fails.

The namespace migrator tool provided by Hippo will allow the user to make the changes to the namespaces that would be otherwise illegal.

## How to migrate difficult namespace changes in a Hippo repository.

First review the Hippo page on the [namespace migrator tool](http://www.onehippo.org/library/upgrade/namespace-migration.html).

Fashion a new cnd file with the properties set as you would wish them.  The cnd file exported from the repository itself is often the best starting point. after the alterations to the cnd are made, the namespace URI will need to be changed.  There will also need to be a different URI for the namespace that you wish to change.  Often just incrementing the version number in the URI accomplishes this.

We have already created 2, one for the change of the exhibit namespace and the public namespace.

This utility is used as part of upgrading UMD Hippo CMS to 7.9.8-0 from 7.8.9-3.
The exhibit:bobyblock and public:htmlfragmentblock namespace entries are changed and the migration
utility is used to update the content of these two document types.

The namespace URL of exhibit is promoted from 'http://lib.umd.edu/exhibit/1.0' to 'http://lib.umd.edu/exhibit/2.0'.
The namespace URL of public is promoted from 'http://www.onehippo.org/public/nt/1.0' to 'http://www.onehippo.org/public/nt/2.0'.

Check the updater that you are going to be using and look at the nodes that need to be changed.  There may need to be additional properties and subnodes that need to be deleted before the change or added after the change.  Many of these can be incorporated into the updater (either before or after a primary type or mixin change).  Some are too difficult for the updater to manage and must be accomplished manually or through other updaters before the namespace migrator can be run.

To build the utility, check out code from git@github.com:umd-lib/public-hippo-namespace-migrator.git.

Make any necessary changes to the updaters or the cnd files.

Use maven to build and package distribution to server.
   
       mvn clean install
       mvn -P migration-dist
       scp target/public-hippo-upgrade-7.9-<version>-migration-distribution.tar.gz <server-host>:<destination>

1. Un-tar the ditribution to any folder (ex, /apps/cms/temp)
   
        cd /apps/cms/temp
        tar zxvf public-hippo-upgrade-7.9-<version>-migration-distribution.tar.gz

2. Copy postgres library

        cd migration
        cp /apps/cms/tomcat-cms/common/lib/postgresql-8.4-702.jdbc4.jar .


3. Fill out the postgres connection properties in migration-repository.xml.  "XXXX" must be replaced for the database name, account, and password entries.

4. Change property file for performing upgrade for some namespace.

        vi migration.properties
   
   Change the properties to either one:
       - migration.cnd=exhibit.cnd
       - migration.cnd=public.cnd
       - migration.updater=edu.umd.lib.hippo.update.ExhibitUpdater
       - migration.updater=edu.umd.lib.hippo.update.PublicUpdater

5. Stop apache and tomcat's or any other utilities that might be accessing the repository.

        cd /apps/cms
        ./control stop

6. Clean up workspace for tomcat-cms

        cd /apps/cms/storage-cms/workspaces
        rm -rf *

7. Execute the migrations

        cd /apps/cms/temp
        bash run-migration

8. If successful, clean up workspace for tomcat-cms.  If the indexes are not in the workspace then go to where they are and delete them in order for them to be recreated.

        cd /apps/cms/storage-cms/workspaces
        rm -rf *

9. Start Hippo website

        cd /apps/cms
        ./control start

9. After cheking the website, repeat from 4 to 9 for another namespace.
