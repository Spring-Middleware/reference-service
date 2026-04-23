import sys
import re
import os
from pathlib import Path

def update_version(new_version):
    root_dir = Path(os.getcwd())
    pom_files = list(root_dir.glob('**/pom.xml'))

    print(f"Updating project to version {new_version}...")

    # Patterns to match
    # 1. Project version: <version>1.4.0</version> (only if it's near the top or belongs to our groupId)
    # 2. Properties: <catalog.version>, <product.version>, <review.version>

    version_tag_pattern = re.compile(r'(?P<pre><version>)1\.4\.0(?P<post></version>)')
    catalog_pattern = re.compile(r'(?P<pre><catalog\.version>)1\.4\.0(?P<post></catalog\.version>)')
    product_pattern = re.compile(r'(?P<pre><product\.version>)1\.4\.0(?P<post></product\.version>)')
    review_pattern = re.compile(r'(?P<pre><review\.version>)1\.4\.0(?P<post></review\.version>)')

    for pom_path in pom_files:
        with open(pom_path, 'r', encoding='utf-8') as f:
            content = f.read()

        original_content = content
        
        # Use named group references to avoid \1[number] ambiguity
        content = version_tag_pattern.sub(fr'\g<pre>{new_version}\g<post>', content)
        content = catalog_pattern.sub(fr'\g<pre>{new_version}\g<post>', content)
        content = product_pattern.sub(fr'\g<pre>{new_version}\g<post>', content)
        content = review_pattern.sub(fr'\g<pre>{new_version}\g<post>', content)


        if content != original_content:
            with open(pom_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Updated: {pom_path.relative_to(root_dir)}")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python update_version.py <new_version>")
        sys.exit(1)

    new_version = sys.argv[1]
    update_version(new_version)
    print("Done!")

