#!/bin/bash

# Package script for play-bootstrap
# Creates JAR files and copies them to the dist/ directory

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DIST_DIR="$SCRIPT_DIR/dist"

# Play 2.9 requires Java 11+
export JAVA_HOME=/Library/Java/JavaVirtualMachines/amazon-corretto-11.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_step() {
    echo -e "${GREEN}==>${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}Warning:${NC} $1"
}

print_error() {
    echo -e "${RED}Error:${NC} $1"
}

usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -c, --clean       Clean before packaging"
    echo "  -a, --all         Package all Play versions (26, 27, 28, 29) and Bootstrap versions"
    echo "  -h, --help        Show this help message"
    echo ""
    echo "By default, packages Play 2.9 modules (Bootstrap 4 and Bootstrap 5)."
    echo "JAR files are collected into the dist/ directory."
}

CLEAN=false
PACKAGE_ALL=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -c|--clean)
            CLEAN=true
            shift
            ;;
        -a|--all)
            PACKAGE_ALL=true
            shift
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            usage
            exit 1
            ;;
    esac
done

package_module() {
    local module_dir=$1
    local module_name=$2

    if [ ! -d "$SCRIPT_DIR/$module_dir" ]; then
        print_warning "Directory $module_dir not found, skipping..."
        return
    fi

    print_step "Packaging $module_name..."
    cd "$SCRIPT_DIR/$module_dir"

    if [ "$CLEAN" = true ]; then
        sbt clean
    fi

    sbt package

    # Copy JAR files to dist/
    local jar_files=$(find target -name "*.jar" -path "*/scala-*/*.jar" ! -name "*-sources.jar" ! -name "*-javadoc.jar" 2>/dev/null)
    for jar_file in $jar_files; do
        cp "$jar_file" "$DIST_DIR/"
        echo -e "  ${GREEN}+${NC} $(basename "$jar_file")"
    done

    cd "$SCRIPT_DIR"
}

echo "========================================"
echo "  Play-Bootstrap Package Script"
echo "========================================"
echo ""

# Prepare dist directory
rm -rf "$DIST_DIR"
mkdir -p "$DIST_DIR"

if [ "$PACKAGE_ALL" = true ]; then
    # Package all versions
    package_module "core-play26" "core-play26"
    package_module "core-play27" "core-play27"
    package_module "core-play28" "core-play28"
    package_module "core-play29" "core-play29"

    package_module "play26-bootstrap3/module" "play26-bootstrap3"
    package_module "play26-bootstrap4/module" "play26-bootstrap4"
    package_module "play27-bootstrap3/module" "play27-bootstrap3"
    package_module "play27-bootstrap4/module" "play27-bootstrap4"
    package_module "play28-bootstrap3/module" "play28-bootstrap3"
    package_module "play28-bootstrap4/module" "play28-bootstrap4"
    package_module "play29-bootstrap4/module" "play29-bootstrap4"
    package_module "play29-bootstrap5/module" "play29-bootstrap5"
else
    # Package Play 2.9 modules (Bootstrap 4 and Bootstrap 5)
    package_module "core-play29" "core-play29"
    package_module "play29-bootstrap4/module" "play29-bootstrap4"
    package_module "play29-bootstrap5/module" "play29-bootstrap5"
fi

echo ""
print_step "Packaging completed!"
echo ""
echo "JAR files collected in: $DIST_DIR/"
echo ""
ls -lh "$DIST_DIR/"*.jar 2>/dev/null || echo "No JAR files found."
