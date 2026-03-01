#!/bin/bash

# Build script for play-bootstrap
# Creates JAR files for core-play29 and play29-bootstrap5 modules

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

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
    echo "  -c, --clean       Clean before building"
    echo "  -t, --test        Run tests after building"
    echo "  -p, --publish     Publish to local Ivy repository"
    echo "  -a, --all         Build all Play versions (26, 27, 28, 29) and Bootstrap versions"
    echo "  -h, --help        Show this help message"
    echo ""
    echo "By default, builds Play 2.9 modules (Bootstrap 4 and Bootstrap 5)."
}

CLEAN=false
TEST=false
PUBLISH_LOCAL=false
BUILD_ALL=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -c|--clean)
            CLEAN=true
            shift
            ;;
        -t|--test)
            TEST=true
            shift
            ;;
        -p|--publish)
            PUBLISH_LOCAL=true
            shift
            ;;
        -a|--all)
            BUILD_ALL=true
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

build_module() {
    local module_dir=$1
    local module_name=$2

    if [ ! -d "$SCRIPT_DIR/$module_dir" ]; then
        print_warning "Directory $module_dir not found, skipping..."
        return
    fi

    print_step "Building $module_name..."
    cd "$SCRIPT_DIR/$module_dir"

    if [ "$CLEAN" = true ]; then
        print_step "Cleaning $module_name..."
        sbt clean
    fi

    sbt compile
    sbt package

    if [ "$TEST" = true ]; then
        print_step "Testing $module_name..."
        sbt test
    fi

    if [ "$PUBLISH_LOCAL" = true ]; then
        print_step "Publishing $module_name to local repository..."
        sbt publishLocal
    fi

    # Find and display JAR location
    local jar_file=$(find target -name "*.jar" -path "*/scala-*/*.jar" ! -name "*-sources.jar" ! -name "*-javadoc.jar" 2>/dev/null | head -1)
    if [ -n "$jar_file" ]; then
        echo -e "${GREEN}JAR created:${NC} $SCRIPT_DIR/$module_dir/$jar_file"
    fi

    cd "$SCRIPT_DIR"
}

echo "========================================"
echo "  Play-Bootstrap Build Script"
echo "========================================"
echo ""

if [ "$BUILD_ALL" = true ]; then
    # Build all versions
    build_module "core-play26" "core-play26"
    build_module "core-play27" "core-play27"
    build_module "core-play28" "core-play28"
    build_module "core-play29" "core-play29"

    build_module "play26-bootstrap3/module" "play26-bootstrap3"
    build_module "play26-bootstrap4/module" "play26-bootstrap4"
    build_module "play27-bootstrap3/module" "play27-bootstrap3"
    build_module "play27-bootstrap4/module" "play27-bootstrap4"
    build_module "play28-bootstrap3/module" "play28-bootstrap3"
    build_module "play28-bootstrap4/module" "play28-bootstrap4"
    build_module "play29-bootstrap4/module" "play29-bootstrap4"
    build_module "play29-bootstrap5/module" "play29-bootstrap5"
else
    # Build Play 2.9 modules (Bootstrap 4 and Bootstrap 5)
    build_module "core-play29" "core-play29"
    build_module "play29-bootstrap4/module" "play29-bootstrap4"
    build_module "play29-bootstrap5/module" "play29-bootstrap5"
fi

echo ""
print_step "Build completed successfully!"
echo ""
echo "JAR files are located in each module's target/scala-X.XX/ directory."
if [ "$PUBLISH_LOCAL" = true ]; then
    echo "Modules have been published to local Ivy repository (~/.ivy2/local/)."
fi