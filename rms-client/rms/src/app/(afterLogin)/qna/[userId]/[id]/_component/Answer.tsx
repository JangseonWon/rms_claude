'use client';

import style from './answer.module.css';
import {useRouter} from "next/navigation";
import {useSession} from "next-auth/react";
import React, {ChangeEvent, useState} from "react";
import {faComment, faFile, faFilePdf, faImage} from "@fortawesome/free-regular-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

export default function Answer() {
    //api 호출해서 정보 가져올거임
    const initialPostData = {
        id: '6e521e46-0686-40b2-a190-9270e69a059e',
        create_at: '2024-01-01',
        last_modify_at: '2024-01-03',
        title: '테스트 입니다',
        number: 1,
        user_id: 'tlsrhkddnd',
        view: 10,
        category_id: 'update',
        content: '알라랄라라라라라\n알라랄라라라라라\n알라랄라라라라라\n알라랄라라라라라\n' +
            '알라랄라라라라라\n알라랄라라라라라\n알라랄라라라라라\n알라랄라라라라라\n' +
            '알라랄라라라라라\n알라랄라라라라라\n알라랄라라라라라\n알라랄라라라라라\n' +
            '알라랄라라라라라\n알라랄라라라라라\n알라랄라라라라라\n알라랄라라라라라\n' +
            '알라랄라라라라라\n알라랄라라라라라\n알라랄라라라라라\n알라랄라라라라라\n' +
            '알라랄라라라라라\n알라랄라라라라라\n알라랄라라라라라\n알라랄라라라라라\n',
        file: [
            {
                id: '3',
                path: '/test/test1/test3',
                name: 'test1.xls',
                create_at: '2024.07.11 14:28:16'
            },
            {
                id: '2',
                path: '/test/test1/test3',
                name: 'test2.pdf',
                create_at: '2024.07.11 14:30:21'
            },
            {
                id: '1',
                path: '/test/test1/test3',
                name: 'test2.img',
                create_at: '2024.07.11 14:32:22'
            },
        ],
        comment: [
            {
                user_id: 'manager',
                create_at: '2024.07.11 14:28:16',
                content: '첫번째'
            },
            {
                user_id: 'tlsrhkddnd',
                create_at: '2024.07.11 14:30:21',
                content: '두번째'
            },
            {
                user_id: 'user',
                create_at: '2024.07.11 14:32:22',
                content: '세번째'
            }
        ]
    };

    const [postData, setPostData] = useState(initialPostData);
    const [commentData, setCommentData] = useState('');
    const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
    const route = useRouter();
    const { data: session } = useSession();
    const writerCheck = session?.user.id === postData.user_id;

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

    return (
        <div className={style.container}>
            <section className={style.userContainer}>
                <label className={style.userLabel}>User</label>
                <label className={style.inputUser}>{postData.user_id}</label>
            </section>
            <section className={style.titleContainer}>
                <label className={style.titleLabel}>Title</label>
                <input
                    className={style.inputTitle}
                    name={'title'}
                    value={postData.title}
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
                    value={postData.content}
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
                    {postData.file.map((file, index) => (
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
                    {postData.comment.map((comment, index) => (
                        <div key={index} className={style.commentUser}>
                            <p className={comment.user_id === 'manager' ? style.commentManagerName : style.commentUserName}>
                                {comment.user_id}
                            </p>
                            <p className={style.commentContent}>{comment.content}</p>
                            <p className={style.commentDate}>{comment.create_at}</p>
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
                        <button className={style.inputCommendButton}>
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