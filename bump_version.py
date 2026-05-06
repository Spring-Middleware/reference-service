import sys
import re
import os
from pathlib import Path

def bump_version(new_version):
    root_dir = Path(os.getcwd())
    pom_files = list(root_dir.glob('**/pom.xml'))

    print(f"Bumping project to version {new_version}...")

    # Pattern for properties (these are safe)
    properties_to_update = [
        'catalog.version',
        'product.version',
        'review.version',
        'catalog.chat.boot.version'
    ]

    for pom_path in pom_files:
        with open(pom_path, 'r', encoding='utf-8') as f:
            content = f.read()

        original_content = content

        # 1. Update properties
        for prop in properties_to_update:
            prop_pattern = re.compile(fr'(?P<pre><{re.escape(prop)}>)\d+\.\d+\.\d+(?P<post></{re.escape(prop)}>)')
            content = prop_pattern.sub(fr'\g<pre>{new_version}\g<post>', content)

        # 2. Update project version (at the top, after groupId io.github.spring-middleware)
        # Search for the main project version or parent version that belongs to us
        project_version_pattern = re.compile(
            r'(<groupId>io\.github\.spring-middleware</groupId>\s*<artifactId>[^<]+</artifactId>\s*<version>)\d+\.\d+\.\d+(</version>)',
            re.DOTALL
        )
        content = project_version_pattern.sub(fr'\g<1>{new_version}\g<2>', content)

        # 3. Update parent version if it matches our groupId
        parent_version_pattern = re.compile(
            r'(<parent>\s*<groupId>io\.github\.spring-middleware</groupId>.*?<version>)\d+\.\d+\.\d+(</version>)',
            re.DOTALL
        )
        content = parent_version_pattern.sub(fr'\g<1>{new_version}\g<2>', content)

        if content != original_content:
            with open(pom_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Updated: {pom_path.relative_to(root_dir)}")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python bump_version.py <new_version>")
        sys.exit(1)

    new_version = sys.argv[1]
    bump_version(new_version)
    print("Done!")
