'use client';

import style from './answer.module.css';
import {usePathname, useRouter} from "next/navigation";
import {useSession} from "next-auth/react";
import React, {ChangeEvent, useCallback, useEffect, useState} from "react";
import {faFile, faFilePdf, faImage} from "@fortawesome/free-regular-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {Post} from "@/model/Post";
import {getPostId} from "@/app/(afterLogin)/qna/[userId]/[id]/_api/getPostId";
import {PostComment} from "@/model/PostComment";
import {fetchComment} from "@/app/(afterLogin)/qna/[userId]/[id]/_api/fetchComment";

export default function Answer() {
    const defaultPostData: Post = {
        id: '',
        create_at: '',
        last_modify_at: '',
        title: '',
        user_id: '',
        post_category_id: '',
        content: '',
        files: [],
        comments: [],
        read: false
    };

    const [postData, setPostData] = useState<Post>(defaultPostData);
    const [commentData, setCommentData] = useState('');
    const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
    const [writerCheck, setWriterCheck] = useState(false);
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
            weekday: 'long',
        };
        return date.toLocaleString('en-US', options);
    }

    const formatContentForTextarea = (content: string) => {
        return content.replace(/\\n/g, '\n').replace(/^'|'$/g, '');
    };

    const editButtonClick = () => {
        if (writerCheck) {
            alert('같어여 수정 가능');
        } else {
            alert('달라서 수정 불가능');
        }
    }

    const deleteButtonClick = () => {
        const confirmed = window.confirm('정말로 삭제하시겠습니까?');
        if (confirmed) {
            route.push('/qna');
        }
    }

    const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setPostData(prevData => ({
            ...prevData,
            [name]: value
        }));
    };

    const handleCommentChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
        setCommentData(e.target.value);
    }

    const handleTextareaChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setPostData(prevData => ({
            ...prevData,
            [name]: value
        }));
    };

    const handleFileNameClick = (id: string) => {
        alert(id + '파일 다운로드');
    }

    const handleCommentClick = async () => {
        const comment : PostComment = {
            post_id: postId,
            content: commentData,
            user_id: session?.user.id
        }
        await fetchComment(comment);
        fetchData();
    }

    const fetchData = useCallback(async () => {
        const response = await getPostId(postId);
        const data = await response.json();
        setPostData(data as Post);
        if (data) {
            if (session?.user?.id === data.user_id) {
                setWriterCheck(true);
            }
        }
    }, [postId, session?.user?.id]);

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    return (
        <div className={style.container}>
            <section className={style.userContainer}>
                <label className={style.userLabel}>User</label>
                <label className={style.inputUser}>{postData?.user_id}</label>
            </section>
            <section className={style.titleContainer}>
                <label className={style.titleLabel}>Title</label>
                <input
                    className={style.inputTitle}
                    name={'title'}
                    value={postData?.title}
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
                    value={formatContentForTextarea(postData.content || '')}
                    onChange={handleTextareaChange}
                    readOnly={!writerCheck}
                ></textarea>
            </section>
            <section className={style.fileContainer}>
                <label className={style.fileLabel}>Upload File</label>
                {writerCheck && (
                    <input
                        type="file"
                        className={style.fileInput}
                        multiple
                    />
                )}
                <ul className={style.fileInput}>
                    {postData?.files?.map((file, index) => (
                        <span
                            key={index}
                            className={style.fileName}
                            onClick={() => handleFileNameClick(file.id)}
                        >
                            {renderFileIcon(file.name)}
                            {file.name}
                        </span>
                    ))}
                </ul>
            </section>
            <section className={style.commentContainer}>
                <label className={style.commentLabel}>Comment</label>
                <div className={style.comment}>
                    {postData?.comments?.map((comment, index) => (
                        <div key={index} className={style.commentUser}>
                            <p className={comment.user_id === 'manager' ? style.commentManagerName : style.commentUserName}>
                                {comment.user_id}
                            </p>
                            <p className={style.commentContent}>{comment.content}</p>
                            <p className={style.commentDate}>{formatDate(comment.create_at ?? '')}</p>
                        </div>
                    ))}
                    <div className={style.firstCommentContainer}>
                        <div className={style.secondCommentContainer}>
                            <p className={style.inputCommentUser}>{session?.user.id}</p>
                            <textarea
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
                <button className={style.cancelButton} onClick={deleteButtonClick}>Delete</button>
            </section>
        </div>
    )
}