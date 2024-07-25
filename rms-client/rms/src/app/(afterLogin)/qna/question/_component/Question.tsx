'use client';

import style from './question.module.css';
import {useSession} from "next-auth/react";
import {useRouter} from "next/navigation";

export default function Question() {
    const route = useRouter();
    const { data: session } = useSession();

    const addButtonClick = () => {
        alert('success');
    }

    const cancelButtonClick = () => {
        const confirmed = window.confirm('정말로 취소하시겠습니까?');
        if (confirmed) {
            route.push('/qna');
        }
    }

    return (
        <div className={style.container}>
            <section className={style.userContainer}>
                <label className={style.userLabel}>User</label>
                <label className={style.inputUser}>{session?.user?.name}</label>
            </section>
            <section className={style.titleContainer}>
                <label className={style.titleLabel}>Title</label>
                <input className={style.inputTitle}></input>
            </section>
            <section className={style.contentContainer}>
                <label className={style.contentLabel}>Content</label>
                <textarea
                    rows={20}
                    name={'content'}
                    className={style.textareaContent}
                ></textarea>
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