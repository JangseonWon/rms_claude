'use client';

import style from './question.module.css';
import {useSession} from "next-auth/react";
import {useRouter} from "next/navigation";
import {fetchPost} from "@/app/(afterLogin)/qna/question/_api/fetchPost";
import {Post} from "@/model/Post";
import React, {ChangeEvent, useState} from "react";

export default function Question() {
    const route = useRouter();
    const { data: session } = useSession();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [categoryId, setCategoryId] =
        useState('f9476263-f8b2-4ff9-b5f9-ed680715401e');

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
            const res = await fetchPost(postData);

            if (res.ok) {
                alert('정상적으로 등록되었습니다.');
                route.push('/qna');
            } else {
                alert('등록 실패하였습니다. 문의바랍니다.');
            }
        }
    }

    const cancelButtonClick = () => {
        const confirmed = window.confirm('정말로 취소하시겠습니까?');
        if (confirmed) {
            route.push('/qna');
        }
    }

    const handleCategoryIdChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const categoryId = event.target.value;
        setCategoryId(categoryId);
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
                        <option value="f9476263-f8b2-4ff9-b5f9-ed680715401e">Service</option>
                        <option value="f1d0a814-8c90-4103-bbd5-6c0e54d37808">Result</option>
                        <option value="7cefa58c-d85b-4f9e-82ff-dc46ba953e27">Bug</option>
                        <option value="f86a9106-e431-4649-a4f9-c61e73ec7bab">Others</option>
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
                <input type="file" className={style.fileInput} />
            </section>
            <section className={style.buttonContainer}>
                <button className={style.addButton} onClick={addButtonClick}>Add</button>
                <button className={style.cancelButton} onClick={cancelButtonClick}>Cancel</button>
            </section>
        </div>
    )
}