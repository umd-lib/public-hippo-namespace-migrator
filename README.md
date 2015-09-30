## Namespace Migrator

There are a number of cases where upgrading Hippo requires a change to one or more namespace cnd files.  If simply importing the files succeeds, then all is fine but there are many cases where the desired change is too large or too fundamental and the simple import of the namespace change fails.

The namespace migrator tool provided by Hippo will allow the user to make the changes to the namespaces that would be otherwise illegal.

## How to migrate difficult namespace changes in a Hippo repository.

First review the Hippo page on the [namespace migrator tool](http://www.onehippo.org/library/upgrade/namespace-migration.html).

Fashion a new cnd file with the properties set as you would wish them.  This will also need to have a different URI for the namespace that you wish to change.  Often just incrementing the version number in the URI accomplishes this.

Load the Hippo Migrator tool into your IDE as a project.

Ceate a java class in your IDE that extends the Basic Updater.  This will test for the changing properties from your corrected namesace.  Within the blocks associated with each node of interest you can code the change of Primary Type, delete properties, and add properties that may be required as the change in the namespace dictates.

Initially the migration is likely to fail or require adjustments and each run can seriously corrupt the repository.  You should make a local copy of the repository to work on until the code is tested and the migration is successful.

Compile the jar file and place it in the file system with your repository.  It does not have to be in any particular location a it is really a self contained utility.

Run the main class in th jar file with the "props" and then "conf" arguments as explained in the Hippo document or the document resulting from the "help" argument.  This will create the sample properties and configuration files.  Modify the files to use them against your repository.

```
java -cp "migrater.jar:postgresql-8.4-702.jdbc4.jar" org.onehippo.cms7.repository.migration.Main help
java -cp "migrater.jar:postgresql-8.4-702.jdbc4.jar" org.onehippo.cms7.repository.migration.Main props > migration.properties
java -cp "migrater.jar:postgresql-8.4-702.jdbc4.jar" org.onehippo.cms7.repository.migration.Main conf > migration.repository.xml
```

- You will probably need to add the 3 jackrabit repository access variables to the properties file (rep.home, wsp.name, and wsp.home).
- Replace the cnd reference and the updater class in the properties file to use the ones you created.
- Try to use only the elements supplied in the sample repository.xml as other elements are generally not needed and my hamper the jar file.
- The Resources branch contain a properties file and a repository.xml hat worked for the UMD postgres repository.  It is missing a few crucial db name, password, and account name values to actually work.

Before running the jar with the migrate argument, clear out the workspaces directory or the default workspace values there will cause the migration to fail.

Now run the migration noting where the migration failed due to properties being either absent or present.  All running processes that access the repository need to be shut down.  The migrator can't run against a repository that other processes are accessing.

```
java -cp "migrater.jar:postgresql-8.4-702.jdbc4.jar" org.onehippo.cms7.repository.migration.Main migrate
```


Modify your updater, rercompile the jar, place it in line to execute, reload the repository, and clear out the workspace to try out the fixes.  Repeat until the repository is cleanly migrated.

Once everything is set, either run the migration locally and install the new repository on the server or run the migration against the server in place.

After running the migration and correcting the repository, rebuild the indexes and clear out the workspaces before restarting the cms and site.  On the local environment, just clearing the workspaces will cause the indexes to be rebuilt.