'use client';

import style from './question.module.css';
import {useSession} from "next-auth/react";
import {useRouter} from "next/navigation";
import {fetchPost} from "@/app/(afterLogin)/qna/question/_api/fetchPost";
import {Post} from "@/model/Post";
import React, {ChangeEvent, useState} from "react";
import {fetchFile} from "@/app/(afterLogin)/qna/question/_api/fetchFile";
import {fetchSendToJandi} from "@/app/(afterLogin)/qna/_api/fetchSendToJandi";

export default function Question() {
    const route = useRouter();
    const { data: session } = useSession();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
    const [categoryId, setCategoryId] =
        useState('f9476263-f8b2-4ff9-b5f9-ed680715401e');
    const [categoryName, setCategoryName] = useState('service');

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
            alert('제목과 내용을 입력해 주세요.');
            return;
        }

        const confirmed = window.confirm('문의 등록하시겠습니까?');
        if (confirmed) {
            const postData: Post = {
                title: title,
                post_category_id: categoryId,
                content: content,
                user_id: session?.user.id
            };

            const postResponse = await fetchPost(postData);
            if (!postResponse.ok) {
                console.error('Post creation failed');
            }

            const postId = (await postResponse.json()).id;
            if (selectedFiles.length > 0) {
                const fileUploadResponse = await fetchFile(postId, selectedFiles);
                if (!fileUploadResponse.ok) {
                    console.error('File upload failed');
                }
            }
            await fetchSendToJandi(session?.user.name!, postId, categoryName, postData);

            alert('정상적으로 등록되었습니다.');
            route.push('/qna');
        }
    };

    const cancelButtonClick = () => {
        const confirmed = window.confirm('정말로 취소하시겠습니까?');
        if (confirmed) {
            route.push('/qna');
        }
    }

    const handleCategoryIdChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const selectedValue = JSON.parse(event.target.value);
        setCategoryId(selectedValue.uuid);
        setCategoryName(selectedValue.category);
    };

    const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
        if (e.target.files) {
            setSelectedFiles(Array.from(e.target.files));
        }
    };

    return (
        <div className={style.container}>
            <section className={style.userAndCategoryContainer}>
                <div className={style.userContainer}>
                    <label className={style.userLabel}>User</label>
                    <label className={style.inputUser}>{session?.user?.name}</label>
                </div>
                <div className={style.select}>
                    <label className={style.categoryLabel}>Category</label>
                    <select className={style.selectCategory} onChange={handleCategoryIdChange}>
                        <option value={JSON.stringify({
                            category: 'service',
                            uuid: 'f9476263-f8b2-4ff9-b5f9-ed680715401e'
                        })}>Service
                        </option>
                        <option value={JSON.stringify({
                            category: 'result',
                            uuid: 'f1d0a814-8c90-4103-bbd5-6c0e54d37808'
                        })}>Result
                        </option>
                        <option value={JSON.stringify({
                            category: 'bug',
                            uuid: '7cefa58c-d85b-4f9e-82ff-dc46ba953e27'
                        })}>Bug
                        </option>
                        <option value={JSON.stringify({
                            category: 'others',
                            uuid: 'f86a9106-e431-4649-a4f9-c61e73ec7bab'
                        })}>Others
                        </option>
                    </select>
                </div>
            </section>
            <section className={style.titleContainer}>
                <label className={style.titleLabel}>Title</label>
                <input
                    className={style.inputTitle}
                    name='title'
                    value={title}
                    onChange={handleInputChange}
                />
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
                <div className={style.fileInput}>
                    <input type="file" multiple onChange={handleFileChange} />
                    {selectedFiles.length > 0 && (
                        <ul className={style.fileList}>
                            {selectedFiles.map((file, index) => (
                                <span key={index} className={style.fileItem}>
                                    {file.name}
                                </span>
                            ))}
                        </ul>
                    )}
                </div>
            </section>
            <section className={style.buttonContainer}>
                <button className={style.addButton} onClick={addButtonClick}>Add</button>
                <button className={style.cancelButton} onClick={cancelButtonClick}>Cancel</button>
            </section>
        </div>
    )
}