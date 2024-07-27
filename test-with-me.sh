#!/bin/bash

show_help() {
    echo "Script to send data to TestsWithMe Android application"
    echo ""
    echo "Options:"
    echo "  -f, --send-file <path>   Specify the path to the file or directory to be sent"
    echo "  -t, --print-ui-tree      Send command to print UI Tree"
    echo "  -h, --help               Display this help message"
}

print_ui_tree() {
    echo "Printing UI Tree..."
    adb shell am broadcast \
        -n com.github.aivanovski.testswithme.android/com.github.aivanovski.testswithme.android.debug.DebugBroadcastReceiver \
        --es "isPrintUiTree" "true"
}

send_file() {
    file_path="$1"

    file_data=$(base64 -i "$file_path")

    echo "Sending file: $file_path"
    adb shell am broadcast \
        -n com.github.aivanovski.testswithme.android/com.github.aivanovski.testswithme.android.debug.DebugBroadcastReceiver \
        --es "testFlowContent" "$file_data"
}

process_send_file() {
    # Check if the argument is a file or directory
    path="$1"
    if [ -d "$path" ]; then
        # If it's a directory, iterate through all files and cat them
        for file in "$path"/*; do
            if [ -f "$file" ]; then
                send_file "$file"
            fi
        done
    elif [ -f "$1" ]; then
        # If it's a file, cat the file
        send_file "$path"
    else
        echo "Error: $path is not a valid file or directory"
        exit 1
    fi
}

if [[ $# -eq 0 ]]; then
    show_help
    exit 1
fi

while [[ $# -gt 0 ]]; do
    case "$1" in
        -f|--send-file)
            process_send_file "$2"
            shift 2
            ;;
        -t|--print-ui-tree)
            print_ui_tree
            exit 0
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

