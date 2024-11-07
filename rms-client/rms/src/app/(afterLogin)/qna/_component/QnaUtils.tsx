import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faFile, faFilePdf} from "@fortawesome/free-regular-svg-icons";
import style from "@/app/(afterLogin)/qna/_component/qnaUtils.module.css";
import {
    faFileAlt,
    faFileExcel,
    faFileImage,
    faFilePowerpoint,
    faFileWord,
    faFileZipper
} from "@fortawesome/free-solid-svg-icons";
import React, {ChangeEvent, DragEvent} from "react";

export const renderFileIcon = (fileName: string): JSX.Element => {
    const extension = fileName.split('.').pop()?.toLowerCase();
    switch (extension) {
        case 'pdf':
            return <FontAwesomeIcon icon={faFilePdf} className={style.filePdfIcon} />;
        case 'jpg':
        case 'jpeg':
        case 'png':
        case 'gif':
            return <FontAwesomeIcon icon={faFileImage} className={style.fileIcon} />;
        case 'xls':
        case 'xlsx':
            return <FontAwesomeIcon icon={faFileExcel} className={style.fileExcelIcon} />;
        case 'doc':
        case 'docx':
            return <FontAwesomeIcon icon={faFileWord} className={style.fileWordIcon} />;
        case 'ppt':
        case 'pptx':
            return <FontAwesomeIcon icon={faFilePowerpoint} className={style.filePowerPointIcon} />;
        case 'txt':
        case 'md':
            return <FontAwesomeIcon icon={faFileAlt} className={style.fileTextIcon} />;
        case 'zip':
            return <FontAwesomeIcon icon={faFileZipper} className={style.fileZipIcon} />;
        default:
            return <FontAwesomeIcon icon={faFile} className={style.fileIcon} />;
    }
};

export const renderAnswerFileIcon = (fileName: string) => {
    const fileExtension = fileName.split('.').pop()?.toLowerCase();

    switch (fileExtension) {
        case 'xls':
        case 'xlsx':
            return <FontAwesomeIcon icon={faFileExcel} className={style.answerExcelIcon} />;
        case 'doc':
        case 'docx':
            return <FontAwesomeIcon icon={faFileWord} className={style.answerWordIcon} />;
        case 'ppt':
        case 'pptx':
            return <FontAwesomeIcon icon={faFilePowerpoint} className={style.answerPowerPointIcon} />;
        case 'txt':
        case 'md':
            return <FontAwesomeIcon icon={faFileAlt} className={style.answerTextIcon} />;
        case 'zip':
            return <FontAwesomeIcon icon={faFileZipper} className={style.answerZipIcon} />;
        case 'img':
        case 'jpg':
        case 'jpeg':
        case 'png':
        case 'gif':
            return <FontAwesomeIcon className={style.answerIcon} icon={faFileImage}/>;
        case 'pdf':
            return <FontAwesomeIcon className={style.answerPdfIcon} icon={faFilePdf}/>;
        default:
            return <FontAwesomeIcon className={style.answerIcon} icon={faFile}/>;
    }
}

export const handleFileChange = (
    e: ChangeEvent<HTMLInputElement>,
    setSelectedFiles: React.Dispatch<React.SetStateAction<File[]>>
) => {
    const files = e.target.files;
    if (files && files.length > 0) {
        setSelectedFiles((prevFiles) => [...prevFiles, ...Array.from(files)]);
    }
};

export const handleDragOver = (
    e: DragEvent<HTMLDivElement>,
    setDragging: React.Dispatch<React.SetStateAction<boolean>>
) => {
    e.preventDefault();
    e.stopPropagation();
    setDragging(true);
};

export const handleDragLeave = (
    e: DragEvent<HTMLDivElement>,
    setDragging: React.Dispatch<React.SetStateAction<boolean>>
) => {
    e.preventDefault();
    e.stopPropagation();
    setDragging(false);
};

export const handleDrop = (
    e: DragEvent<HTMLDivElement>,
    setDragging: React.Dispatch<React.SetStateAction<boolean>>,
    setSelectedFiles: React.Dispatch<React.SetStateAction<File[]>>
) => {
    e.preventDefault();
    e.stopPropagation();
    setDragging(false);
    const droppedFiles = e.dataTransfer.files;
    if (droppedFiles && droppedFiles.length > 0) {
        setSelectedFiles((prevFiles) => [...prevFiles, ...Array.from(droppedFiles)]);
    }
};

export const handleUploadClick = (e: React.MouseEvent) => {
    document.getElementById('fileInput')?.click();
};

export const removeFile = (
    index: number,
    setSelectedFiles: React.Dispatch<React.SetStateAction<File[]>>
) => {
    setSelectedFiles((prevFiles) => prevFiles.filter((_, i) => i !== index));
};