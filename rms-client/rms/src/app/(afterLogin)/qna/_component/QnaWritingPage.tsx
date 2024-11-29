'use client';

import style from '@/css/qna/qnaPost.module.css';
import {useSession} from "next-auth/react";
import {useRouter} from "next/navigation";
import {putPost} from "@/app/(afterLogin)/qna/_api/putPost";
import {Post} from "@/model/Post";
import React, {ChangeEvent, useState} from "react";
import QnaLoading from "@/app/(afterLogin)/qna/_component/QnaLoading";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faArrowLeft, faArrowUpFromBracket, faTimes} from "@fortawesome/free-solid-svg-icons";
import BlueButton from "@/app/_component/BlueButton";
import {
    handleDragLeave,
    handleDragOver,
    handleDrop,
    handleFileChange,
    handleUploadClick,
    removeFile,
    renderFileIcon,
    categoryUUID
} from "@/app/(afterLogin)/qna/_component/QnaUtils";
import {fetchSendToJandi} from "@/app/(afterLogin)/qna/_api/fetchSendToJandi";

interface QnaWritingPageProps {
    category: string;
}

export default function QnaWritingPage({ category }: QnaWritingPageProps) {
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
                        id: categoryUUID(category)
                    },
                    content: content
                };

                await putPost(postData, selectedFiles)
                if (category === 'Q&A') {
                    await fetchSendToJandi(session?.user.name!, 0, 'qna', postData);
                }
            } finally {
                alert('Registered successfully.');
                setIsLoading(false);
                route.push('/qna');
            }
        }
    };

    return (
        <div className={style.container}>
            {isLoading && <QnaLoading/>}
            <section className={style.headerContainer}>
                <h1 className={style.headTitle}>{category}</h1>
                <FontAwesomeIcon className={style.backButton} icon={faArrowLeft} onClick={() => route.push('/qna')}/>
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
                    onDragOver={(e) => handleDragOver(e, setDragging)}
                    onDragLeave={(e) => handleDragLeave(e, setDragging)}
                    onDrop={(e) => handleDrop(e, setDragging, setSelectedFiles)}
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
                                        onChange={(e) => handleFileChange(e, setSelectedFiles)}
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
                                        onClick={() => removeFile(index, setSelectedFiles)}
                                    />
                                    {renderFileIcon(file.name)}
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