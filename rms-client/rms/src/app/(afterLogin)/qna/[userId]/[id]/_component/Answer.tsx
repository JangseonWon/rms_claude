'use client';

import style from './answer.module.css';
import {usePathname, useRouter} from "next/navigation";
import {useSession} from "next-auth/react";
import React, {ChangeEvent, useCallback, useEffect, useState} from "react";
import {faFile, faFilePdf, faImage} from "@fortawesome/free-regular-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {Post} from "@/model/Post";
import {getPostByPostId} from "@/app/(afterLogin)/qna/[userId]/[id]/_api/getPostByPostId";
import {PostComment} from "@/model/PostComment";
import {fetchComment} from "@/app/(afterLogin)/qna/[userId]/[id]/_api/fetchComment";
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {deleteCommentById} from "@/app/(afterLogin)/qna/[userId]/[id]/_api/deleteCommentById";
import {deletePostById} from "@/app/(afterLogin)/qna/[userId]/[id]/_api/deletePostById";
import {getFileById} from "@/app/(afterLogin)/qna/[userId]/[id]/_api/getFileById";
import {deleteFileByPostId} from "@/app/(afterLogin)/qna/[userId]/[id]/_api/deleteFileByPostId";
import {fetchSendToJandi} from "@/app/(afterLogin)/qna/_api/fetchSendToJandi";
import {fetchPostId} from "@/app/(afterLogin)/qna/_api/fetchPostId";
import {fetchFile} from "@/app/(afterLogin)/qna/_api/fetchFile";
import {updatePost} from "@/app/(afterLogin)/qna/[userId]/[id]/_api/updatePost";
import {deleteFileById} from "@/app/(afterLogin)/qna/[userId]/[id]/_api/deleteFileById";
import QnaLoading from "@/app/(afterLogin)/qna/_component/QnaLoading";

export default function Answer() {
    const [postData, setPostData] = useState<Post>();
    const [commentData, setCommentData] = useState('');
    const [writerCheck, setWriterCheck] = useState(false);
    const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const route = useRouter();
    const { data: session } = useSession();

    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const postId = decodeURIComponent(pathSegments.pop() || '');

    const renderFileIcon = (fileName: string) => {
        const fileExtension = fileName.split('.').pop()?.toLowerCase();

        switch (fileExtension) {
            case 'img':
            case 'jpg':
            case 'jpeg':
            case 'png':
            case 'gif':
                return <FontAwesomeIcon className={style.fileIcon} icon={faImage}/>;
            case 'pdf':
                return <FontAwesomeIcon className={style.fileIcon} icon={faFilePdf}/>;
            default:
                return <FontAwesomeIcon className={style.fileIcon} icon={faFile}/>;
        }
    }

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);

        const options: Intl.DateTimeFormatOptions = {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: true,
        };
        return date.toLocaleString('en-US', options);
    }

    const formatContentForTextarea = (content: string) => {
        return content.replace(/\\n/g, '\n').replace(/^'|'$/g, '');
    };

    const editButtonClick = async () => {
        if (writerCheck) {
            const confirmed = window.confirm('정말로 수정하시겠습니까?');
            if (confirmed) {
                setIsLoading(true);
                try {
                    const postId = postData?.id!
                    const postResponse =
                        await updatePost(postId, {title: postData?.title!, content: postData?.content!});
                    if (!postResponse.ok) {
                        console.error('Post creation failed');
                    }
                    if (selectedFiles.length > 0) {
                        const fileUploadResponse = await fetchFile(postId, selectedFiles);
                        if (!fileUploadResponse.ok) {
                            console.error('File upload failed');
                        }
                    }
                    await fetchSendToJandi(session?.user.name!, postId, "update", postData!);

                    alert('정상적으로 수정되었습니다.');
                    route.push('/qna');
                } finally {
                    setIsLoading(false);
                }
            }
        } else {
            alert('수정 불가능 합니다.');
        }
    }

    const deleteButtonClick = async (postId: string) => {
        const confirmed = window.confirm('정말로 삭제하시겠습니까?');
        if (confirmed) {
            setIsLoading(true);
            try {
                await deleteFileByPostId(postId);
                await deletePostById(postId);
            } finally {
                alert('삭제 완료 되었습니다.');
                setIsLoading(false);
                route.push('/qna');
            }
        }
    }

    const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setPostData(prevData => prevData ? { ...prevData, [name]: value } : undefined);
    };

    const handleCommentChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
        setCommentData(e.target.value);
    };

    const handleFileNameClick = async (postId: string, fileId: string) => {
        try {
            const response = await getFileById(postId, fileId);
            if (response.ok) {
                const contentDisposition = response.headers.get('Content-Disposition');
                let filename = fileId;

                if (contentDisposition) {
                    const filenameMatch = contentDisposition.match(/filename[^;=\n]*[=\s](.*?)(;|$)/);
                    if (filenameMatch && filenameMatch[1]) {
                        filename = decodeURIComponent(filenameMatch[1].replace(/"/g, ''));
                    }
                }

                const blob = await response.blob();
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = filename;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                URL.revokeObjectURL(url);
            }
        } catch (error) {
            console.error('Error downloading file:', error);
        }
    }

    const handleCommentDeleteClick = async (postId: string, commentId: string) => {
        const confirmed = window.confirm('댓글을 삭제하시겠습니까?');
        if (confirmed) {
            await deleteCommentById(postId, commentId);
            fetchData();
        }
    }

    const handleFileDeleteClick = async (postId: string, fileId: string) => {
        const confirmed = window.confirm('파일을 삭제하시겠습니까?');
        if (confirmed) {
            setIsLoading(true);
            try{
                await deleteFileById(postId, fileId);
            } finally {
                fetchData();
                setIsLoading(false);
            }
        }
    }

    const handleFileCancelClick = async (fileIndex: number) => {
        setSelectedFiles(prevFiles => prevFiles.filter((_, index) => index !== fileIndex));
    }

    const handleCommentClick = async () => {
        if (commentData.length > 0) {
            const comment : PostComment = {
                post_id: postId,
                content: commentData,
                user_id: session?.user.id
            }
            await fetchComment(comment);
            if (session?.user.role !== "USER") {
                await fetchPostId(postId, true);
            } else {
                await fetchSendToJandi(session?.user.name!, postId, "comment", postData!, comment);
            }

            fetchData();
            setCommentData('');
        } else {
            alert('코멘트 입력해주세요');
        }
    }

    const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
        if (e.target.files) {
            setSelectedFiles(Array.from(e.target.files));
        }
    };

    const fetchData = useCallback(async () => {
        const response = await getPostByPostId(postId);
        const text = await response.text();
        if (text) {
            const data = JSON.parse(text);
            setPostData(data as Post);

            if (session?.user?.id === data.user.id) {
                setWriterCheck(true);
            }
        } else {
            alert('Post not found');
            route.push('/qna');
            return;
        }
    }, [postId, session?.user?.id]);

    useEffect(() => {
        fetchData();
    }, []);

    return (
        <div className={style.container}>
            {isLoading && <QnaLoading/>}
            <section className={style.userContainer}>
                <label className={style.userLabel}>User</label>
                <label className={style.inputUser}>{postData?.user?.name}</label>
            </section>
            <section className={style.titleContainer}>
                <label className={style.titleLabel}>Title</label>
                <input
                    className={style.inputTitle}
                    name={'title'}
                    value={postData?.title || ''}
                    onChange={handleInputChange}
                    readOnly={!writerCheck}
                />
            </section>
            <section className={style.contentContainer}>
                <label className={style.contentLabel}>Content</label>
                <textarea
                    rows={30}
                    name={'content'}
                    className={style.textareaContent}
                    value={formatContentForTextarea(postData?.content || '')}
                    onChange={handleInputChange}
                    readOnly={!writerCheck}
                ></textarea>
            </section>
            <section className={style.fileContainer}>
                <label className={style.fileLabel}>Upload File</label>
                {writerCheck && (
                    <div className={style.fileInput}>
                        <input type="file" className={style.inputButton} multiple onChange={handleFileChange}/>
                        {selectedFiles.length > 0 && (
                            <ul className={style.fileList}>
                                {selectedFiles.map((file, index) => (
                                    <span key={index}>
                                    {file.name}
                                        <FontAwesomeIcon
                                            className={style.fileDelete}
                                            icon={faXmark}
                                            onClick={()=> handleFileCancelClick(index)}
                                        />
                                </span>
                                ))}
                            </ul>
                        )}
                    </div>
                )}
                <ul className={style.fileInput}>
                    {postData?.files?.map((file, index) => (
                        <div className={style.fileItem} key={index}>
                            <span
                                key={index}
                                className={style.fileName}
                                onClick={() => handleFileNameClick(file.post_id, file.id)}
                            >
                            {renderFileIcon(file.name)}
                                {file.name}
                            </span>
                            {session?.user?.role === 'USER' && (<FontAwesomeIcon
                                className={style.fileDelete}
                                icon={faXmark}
                                onClick={()=> handleFileDeleteClick(file.post_id, file.id)}
                            />)}
                        </div>
                    ))}
                </ul>
            </section>
            <section className={style.commentContainer}>
                <label className={style.commentLabel}>Comment</label>
                <div className={style.comment}>
                    {postData?.comments?.map((comment, index) => (
                        <div key={index} className={style.commentUser}>
                            <div className={style.commentNameAndDelete}>
                                <p className={comment.user_id === 'manager' ? style.commentManagerName : style.commentUserName}>
                                    {comment.user_id}
                                </p>
                                {comment.user_id === session?.user?.id && (
                                    <FontAwesomeIcon
                                        className={style.commentDelete}
                                        icon={faXmark}
                                        onClick={()=> handleCommentDeleteClick(comment.post_id!!, comment.id!!)}
                                    />
                                )}
                            </div>
                            <pre className={style.commentContent}>{comment.content}</pre>
                            <p className={style.commentDate}>{formatDate(comment.create_at ?? '')}</p>
                        </div>
                    ))}
                    <div className={style.firstCommentContainer}>
                        <div className={style.secondCommentContainer}>
                            <p className={style.inputCommentUser}>{session?.user.id}</p>
                            <textarea
                                value={commentData}
                                rows={5}
                                className={style.inputComment}
                                name={'comment'}
                                onChange={handleCommentChange}
                            />
                        </div>
                        <button className={style.inputCommendButton} onClick={handleCommentClick}>
                            Comment
                        </button>
                    </div>
                </div>
            </section>
            <section className={style.buttonContainer}>
                {writerCheck && (
                    <button className={style.editButton} onClick={editButtonClick}>
                        Edit Post
                    </button>
                )}
                <button className={style.cancelButton} onClick={()=> deleteButtonClick(postData?.id!)}>Delete</button>
            </section>
        </div>
    )
}