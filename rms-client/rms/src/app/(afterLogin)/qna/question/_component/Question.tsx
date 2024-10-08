'use client';

import style from './question.module.css';
import {useSession} from "next-auth/react";
import {useRouter} from "next/navigation";
import {putPost} from "@/app/(afterLogin)/qna/_api/putPost";
import {Post} from "@/model/Post";
import React, {ChangeEvent, useState} from "react";
import {fetchSendToJandi} from "@/app/(afterLogin)/qna/_api/fetchSendToJandi";
import QnaLoading from "@/app/(afterLogin)/qna/_component/QnaLoading";

export default function Question() {
    const route = useRouter();
    const { data: session } = useSession();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
    const [categoryId, setCategoryId] = useState('f9476263-f8b2-4ff9-b5f9-ed680715401e');
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
                        id: categoryId
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

    const cancelButtonClick = () => {
        const confirmed = window.confirm('Are you sure you want to cancel?');
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
            {isLoading && <QnaLoading/>}
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