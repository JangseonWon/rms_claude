'use client';

import style from './question.module.css';
import {useSession} from "next-auth/react";
import {useRouter} from "next/navigation";
import {putPost} from "@/app/(afterLogin)/qna/_api/putPost";
import {Post} from "@/model/Post";
import React, {ChangeEvent, DragEvent, useState} from "react";
import QnaLoading from "@/app/(afterLogin)/qna/_component/QnaLoading";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faArrowLeft,
    faArrowUpFromBracket,
    faFile,
    faFileAlt,
    faFileExcel,
    faFileImage,
    faFilePdf,
    faFilePowerpoint,
    faFileWord,
    faFileZipper,
    faTimes
} from "@fortawesome/free-solid-svg-icons";
import BlueButton from "@/app/_component/BlueButton";

export default function Question() {
    const route = useRouter();
    const { data: session } = useSession();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
    const [dragging, setDragging] = useState(false);

    const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        if (name === 'title') {
            setTitle(value);
        } else if (name === 'content') {
            setContent(value);
        }
    };

    const addButtonClick = async () => {
        if (!title.trim() || !content.trim()) {
            alert('Please enter the title and content.');
            return;
        }

        const confirmed = window.confirm('Would you like to register your inquiry?');
        if (confirmed) {
            setIsLoading(true);
            try {
                const postData: Post = {
                    title: title,
                    post_category: {
                        id: 'adfe53d3-a816-44ed-a318-7f33d7965614'
                    },
                    content: content
                };

                await putPost(postData, selectedFiles)
                //await fetchSendToJandi(session?.user.name!, postId, categoryName, postData);
            } finally {
                alert('Registered successfully.');
                setIsLoading(false);
                route.push('/qna');
            }
        }
    };

    const removeFile = (index: number) => {
        setSelectedFiles((prevFiles) => prevFiles.filter((_, i) => i !== index));
    };

    const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
        const files = e.target.files;
        if (files && files.length > 0) {
            setSelectedFiles((prevFiles) => [...prevFiles, ...Array.from(files)]);
        }
    };

    const handleDragOver = (e: DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        e.stopPropagation();
        setDragging(true);
    };

    const handleDragLeave = (e: DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        e.stopPropagation();
        setDragging(false);
    };

    const handleDrop = (e: DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        e.stopPropagation();
        setDragging(false);
        const droppedFiles = e.dataTransfer.files;
        if (droppedFiles && droppedFiles.length > 0) {
            setSelectedFiles((prevFiles) => [...prevFiles, ...Array.from(droppedFiles)]);
        }
    };

    const handleUploadClick = () => {
        document.getElementById('fileInput')?.click();
    };

    const getFileIcon = (fileName: string) => {
        const extension = fileName.split('.').pop()?.toLowerCase();
        switch (extension) {
            case 'pdf':
                return <FontAwesomeIcon icon={faFilePdf} className={style.filePdfIcon}/>;
            case 'jpg':
            case 'jpeg':
            case 'png':
            case 'gif':
                return <FontAwesomeIcon icon={faFileImage} className={style.fileIcon}/>;
            case 'xls':
            case 'xlsx':
                return <FontAwesomeIcon icon={faFileExcel} className={style.fileExcelIcon}/>;
            case 'doc':
            case 'docx':
                return <FontAwesomeIcon icon={faFileWord} className={style.fileWordIcon}/>;
            case 'ppt':
            case 'pptx':
                return <FontAwesomeIcon icon={faFilePowerpoint} className={style.filePowerPointIcon}/>;
            case 'txt':
            case 'md':
                return <FontAwesomeIcon icon={faFileAlt} className={style.fileTextIcon}/>;
            case 'zip':
                return <FontAwesomeIcon icon={faFileZipper} className={style.fileZipIcon}/>;
            default:
                return <FontAwesomeIcon icon={faFile} className={style.fileIcon}/>;
        }
    };

    return (
        <div className={style.container}>
            {isLoading && <QnaLoading/>}
            <section className={style.headerContainer}>
                <h1 className={style.headTitle}>Q&A</h1>
                <FontAwesomeIcon className={style.backButton} icon={faArrowLeft} onClick={() => route.back()}/>
            </section>
            <section className={style.buttonContainer}>
                <BlueButton name={"POST"} onClick={addButtonClick}/>
            </section>
            <section className={style.userAndTitleContainer}>
                <div className={style.userContainer}>
                    <label className={style.userLabel}>User</label>
                    <label className={style.inputUser}>{session?.user?.name}</label>
                </div>
                <div className={style.titleContainer}>
                    <label className={style.titleLabel}>Title</label>
                    <input
                        className={style.inputTitle}
                        name='title'
                        value={title}
                        onChange={handleInputChange}
                    />
                </div>
            </section>
            <section className={style.contentContainer}>
                <label className={style.contentLabel}>Content</label>
                <textarea
                    rows={20}
                    name='content'
                    className={style.textareaContent}
                    value={content}
                    onChange={handleInputChange}
                />
            </section>
            <section className={style.fileContainer}>
                <label className={style.fileLabel}>Upload File</label>
                <div
                    className={`${style.fileUploadBody} ${dragging ? style.dragging : ''}`}
                    onDragOver={handleDragOver}
                    onDragLeave={handleDragLeave}
                    onDrop={handleDrop}
                >
                    {selectedFiles.length === 0 ? (
                        <>
                            <FontAwesomeIcon style={{fontSize: '40px'}} icon={faArrowUpFromBracket}/>
                            <div className={style.word}>Drag and drop</div>
                            <div className={style.selectLink}>
                                <div>or&nbsp;</div>
                                <>
                                    <input
                                        type="file"
                                        id="fileInput"
                                        multiple
                                        style={{display: 'none'}}
                                        onChange={handleFileChange}
                                    />
                                    <div className={style.link} onClick={handleUploadClick}>Select file</div>
                                </>
                            </div>
                        </>
                    ) : (
                        <ul className={style.fileList}>
                            {selectedFiles.map((file, index) => (
                                <li key={index} className={style.fileItem}>
                                    <FontAwesomeIcon
                                        icon={faTimes}
                                        className={style.deleteIcon}
                                        onClick={() => removeFile(index)}
                                    />
                                    {getFileIcon(file.name)}
                                    <div className={style.fileName}>{file.name}</div>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </section>
        </div>
    )
}