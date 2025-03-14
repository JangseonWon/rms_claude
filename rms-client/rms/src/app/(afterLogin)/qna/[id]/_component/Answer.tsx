'use client';

import answerStyle from './answer.module.css';
import style from "@/css/qna/qnaPost.module.css";
import {usePathname, useRouter} from "next/navigation";
import {useSession} from "next-auth/react";
import React, {ChangeEvent, useCallback, useEffect, useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {Post} from "@/model/Post";
import {getPostByPostId} from "@/app/(afterLogin)/qna/[id]/_api/getPostByPostId";
import {putComment} from "@/app/(afterLogin)/qna/[id]/_api/putComment";
import {faArrowLeft, faXmark} from "@fortawesome/free-solid-svg-icons";
import {deleteCommentById} from "@/app/(afterLogin)/qna/[id]/_api/deleteCommentById";
import {deletePostById} from "@/app/(afterLogin)/qna/[id]/_api/deletePostById";
import {getPostFile} from "@/app/(afterLogin)/qna/[id]/_api/getPostFile";
import {updatePost} from "@/app/(afterLogin)/qna/[id]/_api/updatePost";
import {deletePostFileById} from "@/app/(afterLogin)/qna/[id]/_api/deletePostFileById";
import QnaLoading from "@/app/(afterLogin)/qna/_component/QnaLoading";
import {PostFile} from "@/model/PostFile";
import {Role} from "@/model/Role";
import {Comment} from "@/model/Comment";
import BlueButton from "@/app/_component/BlueButton";
import {renderAnswerFileIcon} from "@/app/(afterLogin)/qna/_component/QnaUtils";
import GreenButton from "@/app/_component/GreenButton";
import {fetchSendToJandi} from "@/app/(afterLogin)/qna/_api/fetchSendToJandi";
import {PostComment} from "@/model/PostComment";
import {putPostReadChangeNew} from "@/app/(afterLogin)/qna/_api/putPostReadChangeNew";
import {putPostReadByUserId} from "@/app/(afterLogin)/qna/_api/putPostReadByUserId";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import {
    useOkNotice,
    useOpenNoticeDialog,
    useSetMessageNoticeDialog,
    useSetOkNotice
} from "@/store/useNoticeDialogStore";

export default function Answer() {
    const [postData, setPostData] = useState<Post>();
    const [commentData, setCommentData] = useState<Comment>();
    const [writerCheck, setWriterCheck] = useState(false);
    const [deletePostId, setDeletePostId] = useState<string | undefined>();
    const [deleteCommentId, setDeleteCommentId] = useState<string | undefined>();
    const [deleteFileId, setDeleteFileId] = useState<string | undefined>();
    const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const showAlert = CallAlertDialog();
    const setShowNoticeDialog = useOpenNoticeDialog();
    const setNoticeMessage = useSetMessageNoticeDialog();
    const okNotice = useOkNotice();
    const setOkNotice = useSetOkNotice();
    const route = useRouter();
    const {data: session} = useSession();

    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const postId = parseInt(decodeURIComponent(pathSegments.pop() || "0"), 10);

    const MAX_FILE_SIZE = 50 * 1024 * 1024;

    const formatDate = (date: Date) => {
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
            setIsLoading(true);
            try {
                await updatePost(postData!, selectedFiles);
                await putPostReadChangeNew(postId);
                await fetchSendToJandi(session?.user?.name!, postId, "update", postData!);
                showAlert('It has been corrected properly.');
                route.push('/qna');
            } finally {
                setIsLoading(false);
            }
        } else {
            showAlert('Modification is not possible.');
        }
    }

    const handleDeleteClick = (id: string, type: string) => {
        switch (type) {
            case 'post':
                setDeletePostId(id);
                setNoticeMessage('Are you sure you want to delete it?');
                break;
            case 'comment':
                setDeleteCommentId(id);
                setNoticeMessage('Are you sure you want to delete your comment?');
                break;
            case 'file':
                setDeleteFileId(id);
                setNoticeMessage('Are you sure you want to delete the file?');
                break;
        }
        setShowNoticeDialog(true);
    };

    const deleteItem = async (id: string, type: string) => {
        setIsLoading(true);
        try {
            if (type === 'post') await deletePostById(id);
            else if (type === 'comment') await deleteCommentById(id);
            else if (type === 'file') await deletePostFileById(id);
            showAlert('Deletion has been completed.');
        } finally {
            setIsLoading(false);
            if (type === 'post') route.push('/qna');
        }
    };

    const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setPostData(prevData => prevData ? { ...prevData, [name]: value } : undefined);
    };

    const handleFileNameClick = async (postFile: PostFile) => {
        try {
            const response = await getPostFile(postFile.id!);
            if (response.ok) {
                const blob = await response.blob();
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = postFile.name!;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                URL.revokeObjectURL(url);
            }
        } catch (error) {
            console.error('Error downloading file:', error);
        }
    }

    const handleFileCancelClick = async (fileIndex: number) => {
        setSelectedFiles(prevFiles => prevFiles.filter((_, index) => index !== fileIndex));
    }

    const handleCommentClick = async () => {
        if (commentData) {
            const comment : PostComment = {
                post_id: postId,
                content: commentData.content,
                user_id: session?.user.id
            }
            await putComment(postId, commentData);
            await putPostReadChangeNew(postId);
            await fetchSendToJandi(session?.user.name!, postId, "comment", postData!, comment);
            fetchData();
            setCommentData(undefined);
        } else {
            showAlert('Please enter a comment');
        }
    }

    const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
        if (e.target.files) {
            const selectedFiles = Array.from(e.target.files);
            const validFiles = selectedFiles.filter(file => {
                if (file.size > MAX_FILE_SIZE) {
                    showAlert(`${file.name} size exceeds 50 MB.`);
                    return false;
                }
                return true;
            });
            setSelectedFiles(validFiles);
        }
    };

    const fetchData = useCallback(async () => {
        try {
            const response = await getPostByPostId(postId);
            if (!response.ok) new Error('Failed to fetch post');

            const text = await response.text();
            if (!text) new Error('Post not found');

            const data = JSON.parse(text);
            setPostData(data as Post);

            if (session?.user?.id === data.user.id) {
                setWriterCheck(true);
            }
        } catch (error) {
            console.error('Error fetching post:', error);
            showAlert('An error occurred while fetching the post');
            route.push('/qna');
        }
    }, [postId, session?.user?.id]);

    useEffect(() => {
        putPostReadByUserId(postId);
        fetchData();
    }, []);

    useEffect(() => {
        if (session?.user?.id) {
            fetchData();
        }
    }, [session?.user?.id]);

    useEffect(() => {
        if (okNotice) {
            (async () => {
                if (deleteCommentId) await deleteItem(deleteCommentId, "comment");
                if (deleteFileId) await deleteItem(deleteFileId, "file");
                if (deletePostId) await deleteItem(deletePostId, "post");

                setDeletePostId(undefined);
                setDeleteCommentId(undefined);
                setDeleteFileId(undefined);

                await fetchData();
                setOkNotice(false);
            })();
        }
    }, [okNotice]);

    return (
        <div className={style.container}>
            {isLoading && <QnaLoading/>}
            <section className={style.headerContainer}>
                <h1 className={style.headTitle}>Q&A</h1>
                <FontAwesomeIcon className={style.backButton} icon={faArrowLeft} onClick={() => route.push('/qna')}/>
            </section>
                {writerCheck && (
                    <section className={style.buttonContainer}>
                        <BlueButton name={"EDIT"} onClick={editButtonClick}/>
                        <GreenButton name={"DELETE"} onClick={() => handleDeleteClick(postData?.id?.toString()!, 'post')}/>
                    </section>
                )}
                    <section className={style.userAndTitleContainer}>
                <div className={style.userContainer}>
                    <label className={style.userLabel}>User</label>
                    <label className={style.inputUser}>{postData?.user?.name}</label>
                </div>
                <div className={style.titleContainer}>
                    <label className={style.titleLabel}>Title</label>
                    <input
                        className={style.inputTitle}
                        name={'title'}
                        value={postData?.title || ''}
                        onChange={handleInputChange}
                        readOnly={!writerCheck}
                    />
                </div>
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
                    <div className={answerStyle.fileInput}>
                        <label htmlFor={'file'}>
                            <div className={answerStyle.fileUploadLabel}>File Upload</div>
                        </label>
                        <input name="file" id="file" type="file" className={answerStyle.inputButton}
                               multiple onChange={handleFileChange}
                        />
                        {selectedFiles.length > 0 && (
                            <ul className={style.fileList}>
                                {selectedFiles.map((file, index) => (
                                    <span key={index}>
                                    {file.name}
                                        <FontAwesomeIcon
                                            className={answerStyle.fileDelete}
                                            icon={faXmark}
                                            onClick={() => handleFileCancelClick(index)}
                                        />
                                </span>
                                ))}
                            </ul>
                        )}
                    </div>
                )}
                <ul className={style.fileInput}>
                    {postData?.post_files?.map((file, index) => (
                        <div className={answerStyle.fileItem} key={index}>
                            <span
                                key={index}
                                className={answerStyle.fileName}
                                onClick={() => handleFileNameClick(file)}
                            >
                            {renderAnswerFileIcon(file.name!)}{file.name}
                            </span>
                            {session?.user?.id === postData.user!!.id && (<FontAwesomeIcon
                                className={answerStyle.fileDelete}
                                icon={faXmark}
                                onClick={() => handleDeleteClick(file.id!, 'file')}
                            />)}
                        </div>
                    ))}
                </ul>
            </section>
            <section className={answerStyle.commentContainer}>
                <label className={answerStyle.commentLabel}>Comment</label>
                <div className={answerStyle.comment}>
                    {postData?.comments?.map((comment, index) => (
                        <div key={index} className={answerStyle.commentUser}>
                            <div className={answerStyle.commentNameAndDelete}>
                                <p className={comment.user?.role === Role.MANAGER ? answerStyle.commentManagerName : answerStyle.commentUserName}>
                                    {comment.user?.id}
                                </p>
                                {comment.user?.id === session?.user?.id && (
                                    <FontAwesomeIcon
                                        className={answerStyle.commentDelete}
                                        icon={faXmark}
                                        onClick={() => handleDeleteClick(comment.id!, 'comment')}
                                    />
                                )}
                            </div>
                            <pre className={answerStyle.commentContent}>{comment.content}</pre>
                            <p className={answerStyle.commentDate}>{formatDate(comment.create_at!)}</p>
                        </div>
                    ))}
                    <div className={answerStyle.firstCommentContainer}>
                        <div className={answerStyle.secondCommentContainer}>
                            <p className={answerStyle.inputCommentUser}>{session?.user.id}</p>
                            <textarea
                                value={commentData?.content || ''}
                                rows={5}
                                className={answerStyle.inputComment}
                                name={'comment'}
                                onChange={e => setCommentData({content: e.target.value})}
                            />
                        </div>
                        <button className={answerStyle.inputCommentButton} onClick={handleCommentClick}>
                            Comment
                        </button>
                    </div>
                </div>
            </section>
        </div>
    )
}