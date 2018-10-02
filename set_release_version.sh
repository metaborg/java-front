#!/usr/bin/env sh

set -eu

if [ "$#" -eq "0" ]; then
  echo "Usage: $0 VERSION\nReplaces \${VERSION}-SNAPSHOT with \$VERSION, updates Spoofax to latest release version. "
  exit 0
fi

NEW_VERSION=$1

sed -i '' "s/$NEW_VERSION-SNAPSHOT/$NEW_VERSION/g" lang.java/metaborg.yaml
sed -i '' "s/$NEW_VERSION-SNAPSHOT/$NEW_VERSION/g" lang.java.example/metaborg.yaml
sed -i '' "s/$NEW_VERSION-SNAPSHOT/$NEW_VERSION/g" lang.java.test/metaborg.yaml
sed -i '' "s/$NEW_VERSION-SNAPSHOT/$NEW_VERSION/g" .mvn/extensions.xml

# Update Spoofax to newest released version
mvn -f pom.xml versions:update-parent -DgenerateBackupPoms=false
# Update java-front version to new version
mvn -f pom.xml versions:set -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false
# This part is ideally done in one go by the previous command but then we need the poms to be point to the above aggregator as a parent. Maybe look at the setup of PIE some time to set that up.
mvn -f lang.java/pom.xml versions:update-parent -DgenerateBackupPoms=false
mvn -f lang.java/pom.xml versions:set -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false
mvn -f lang.java.example/pom.xml versions:update-parent -DgenerateBackupPoms=false
mvn -f lang.java.example/pom.xml versions:set -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false
mvn -f lang.java.test/pom.xml versions:update-parent -DgenerateBackupPoms=false
mvn -f lang.java.test/pom.xml versions:set -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false
mvn -f lang.java.eclipse/pom.xml versions:set -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false
mvn -f lang.java.eclipse.feature/pom.xml versions:set -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false
# Doesn't work for some reason
#mvn -f lang.java.eclipse.site/pom.xml versions:set -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false
sed -i '' "s/$NEW_VERSION-SNAPSHOT/$NEW_VERSION/g" lang.java.eclipse.site/pom.xml