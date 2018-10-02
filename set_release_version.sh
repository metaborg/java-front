#!/usr/bin/env sh

set -eu

if [ "$#" -eq "0" ]; then
  echo "USAGE:\n    $0 VERSION SPOOFAX_VERSION\n    $0 OLD_VERSION NEW_VERSION SPOOFAX_VERSION\n\nThe first option assumes you are on the given version and just want to strip off the -SNAPSHOT. \n\nReplaces \${OLD_VERSION}-SNAPSHOT with \$NEW_VERSION, updates Spoofax to latest release version. \n\$SPOOFAX_VERSION should also be this latest release version to update a file with that cannot be auto-updated with maven. "
  exit 0
fi

if [ "$#" -eq "3" ]; then
  OLD_VERSION=$1
  NEW_VERSION=$2
  SPOOFAX_VERSION=$3
else
  OLD_VERSION=$1
  NEW_VERSION=$1
  SPOOFAX_VERSION=$2
fi

sed -i '' "s/$OLD_VERSION-SNAPSHOT/$NEW_VERSION/g" lang.java/metaborg.yaml
sed -i '' "s/$OLD_VERSION-SNAPSHOT/$NEW_VERSION/g" lang.java.example/metaborg.yaml
sed -i '' "s/$OLD_VERSION-SNAPSHOT/$NEW_VERSION/g" lang.java.test/metaborg.yaml
sed -i '' "s|<version>.*</version>|<version>$SPOOFAX_VERSION</version>|g" .mvn/extensions.xml

sed -i '' "s/$OLD_VERSION.qualifier/$NEW_VERSION/g" lang.java.eclipse.site/site.xml
sed -i '' "s/$OLD_VERSION.qualifier/$NEW_VERSION/g" lang.java.eclipse.feature/feature.xml
sed -i '' "s/$OLD_VERSION.qualifier/$NEW_VERSION/g" lang.java.eclipse/META-INF/MANIFEST.MF

# Update Spoofax to newest released version
mvn -f pom.xml versions:update-parent -DgenerateBackupPoms=false
mvn -f lang.java/pom.xml versions:update-parent -DgenerateBackupPoms=false
mvn -f lang.java.example/pom.xml versions:update-parent -DgenerateBackupPoms=false
mvn -f lang.java.test/pom.xml versions:update-parent -DgenerateBackupPoms=false
# Update java-front version to new version
mvn -f pom.xml versions:set -DnewVersion=$NEW_VERSION -DprocessAllModules=true -DgenerateBackupPoms=false