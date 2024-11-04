'use client';

import style from './faqPage.module.css';
import {usePathname, useRouter} from "next/navigation";
import {useSession} from "next-auth/react";
import React, {useCallback, useEffect, useState} from "react";
import {faFile, faFilePdf, faImage} from "@fortawesome/free-regular-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {Post} from "@/model/Post";
import {getPostByPostId} from "@/app/(afterLogin)/qna/[userId]/[id]/_api/getPostByPostId";
import {getPostFile} from "@/app/(afterLogin)/qna/[userId]/[id]/_api/getPostFile";
import {PostFile} from "@/model/PostFile";

export default function FaqPage() {
    const [postData, setPostData] = useState<Post>();
    const route = useRouter();
    const {data: session} = useSession();

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

    const formatContentForTextarea = (content: string) => {
        return content.replace(/\\n/g, '\n').replace(/^'|'$/g, '');
    };

    const handleFileNameClick = async (postFile: PostFile) => {
        try {
            const response = await getPostFile(postFile.id!);
            if (response.ok) {
                const contentDisposition = response.headers.get('Content-Disposition');
                let filename = postFile.name;

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

    const fetchData = useCallback(async () => {
        const response = await getPostByPostId(postId);
        const text = await response.text();
        if (text) {
            const data = JSON.parse(text);
            setPostData(data as Post);

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
            <section className={style.titleContainer}>
                <div className={style.category}>
                    <label className={style.titleLabel}>Category</label>
                    <input
                        className={style.inputTitle}
                        name={'title'}
                        value={postData?.title || ''}
                        readOnly={true}
                    />
                </div>
                <div className={style.title}>
                    <label className={style.titleLabel}>Title</label>
                    <input
                        className={style.inputTitle}
                        name={'title'}
                        value={postData?.title || ''}
                        readOnly={true}
                    />
                </div>
            </section>
            <section className={style.contentContainer}>
                <textarea
                    rows={30}
                    name={'content'}
                    className={style.textareaContent}
                    value={formatContentForTextarea(postData?.content || '')}
                    readOnly={true}
                ></textarea>
            </section>
            <section className={style.fileContainer}>
                <label className={style.fileLabel}>Upload File</label>
                <ul className={style.fileInput}>
                    {postData?.post_files?.map((file, index) => (
                        <div className={style.fileItem} key={index}>
                            <span
                                key={index}
                                className={style.fileName}
                                onClick={() => handleFileNameClick(file)}
                            >
                            {renderFileIcon(file.name!)}{file.name}
                            </span>
                        </div>
                    ))}
                </ul>
            </section>
        </div>
    )
}