import { useState } from "react";
import { createPortal } from "react-dom";
import ConfirmDialog from "./ConfirmDialog";

export function useConfirmDialog() {
    const [isOpen, setIsOpen] = useState(false);
    const [title, setTitle] = useState("");
    const [message, setMessage] = useState("");
    const [resolver, setResolver] = useState<((result: boolean) => void) | null>(null);

    const confirm = (title: string, message: string): Promise<boolean> => {
        setTitle(title);
        setMessage(message);
        setIsOpen(true);
        return new Promise<boolean>((resolve) => {
            setResolver(() => resolve);
        });
    };

    const handleConfirm = () => {
        if (resolver) resolver(true);
        closeDialog();
    };

    const handleCancel = () => {
        if (resolver) resolver(false);
        closeDialog();
    };

    const closeDialog = () => {
        setIsOpen(false);
        setTitle("");
        setMessage("");
        setResolver(null);
    };

    const dialogComponent = isOpen
        ? createPortal(
            <ConfirmDialog
                title={title}
                message={message}
                onConfirm={handleConfirm}
                onCancel={handleCancel}
            />,
            document.body
        )
        : null;

    return { confirm, dialogComponent };
}