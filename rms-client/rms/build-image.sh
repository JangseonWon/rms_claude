#!/bin/bash

# Check if at least 3 arguments are passed
if [ "$#" -lt 3 ]; then
    echo "Usage: build-image.sh <runtime> [-u <username>] [-p <password>] -i <image_name> -r <registry>"
    exit 1
fi

# Initialize variables
RUNTIME=$1

if ! command -v $RUNTIME &> /dev/null; then
    echo "Error: $RUNTIME is not installed or not in PATH."
    exit 1
fi

USERNAME=""
PASSWORD=""
IMAGE_NAME=""
REGISTRY=""
BRANCH="default"

# Parse command-line arguments
shift # Remove the runtime from the arguments
while [ "$#" -gt 0 ]; do
    case "$1" in
        -u)
            USERNAME="$2"
            shift 2
            ;;
        -p)
            PASSWORD="$2"
            shift 2
            ;;
        -i)
            IMAGE_NAME="$2"
            shift 2
            ;;
        -r)
            REGISTRY="$2"
            shift 2
            ;;
        --auth-secret)
            AUTH_SECRET="$2"
            shift 2
            ;;
        --base-url)
            BASE_URL="$2"
            shift 2
            ;;
        --next-public-ws-base-url)
            NEXT_PUBLIC_WS_BASE_URL="$2"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

# Validate that all required arguments are provided
if [ -z "$IMAGE_NAME" ] || [ -z "$REGISTRY" ]; then
    echo "Options -i and -r must be provided. Usage: $0 <runtime> [-u <username>] [-p <password>] -i <image_name> -r <registry>"
    exit 1
fi

# Prompt for password if username is provided but password is not
if [ -n "$USERNAME" ] && [ -z "$PASSWORD" ]; then
    read -s -p "Enter Password: " PASSWORD
    echo
fi

# Log in to the registry if both username and password are provided
if [ -n "$USERNAME" ] && [ -n "$PASSWORD" ]; then
    echo "$PASSWORD" | $RUNTIME login -u "$USERNAME" --password-stdin "$REGISTRY"
fi

# Build the image
$RUNTIME build \
--build-arg AUTH_SECRET=$AUTH_SECRET \
--build-arg BASE_URL=$BASE_URL \
--build-arg NEXT_PUBLIC_WS_BASE_URL=$NEXT_PUBLIC_WS_BASE_URL \
-t "$REGISTRY/$IMAGE_NAME" . || { echo "Build failed"; exit 1; }

# Push the image to the registry
$RUNTIME push "$REGISTRY/$IMAGE_NAME" || { echo "Push failed"; exit 1; }

# Log out from the registry if username was provided
if [ -n "$USERNAME" ]; then
    $RUNTIME logout "$REGISTRY" || { echo "Logout failed"; exit 1; }
fi


echo "Image $IMAGE_NAME has been pushed to $REGISTRY"
