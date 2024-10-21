"use client"

import React, {useCallback, useEffect, useState} from "react";
import style from "./postTable.module.css";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {Query} from "@/model/Query";
import {Post} from "@/model/Post";
import {useRouter} from "next/navigation";
import {faComment} from "@fortawesome/free-regular-svg-icons";
import {postPosts} from "@/app/(afterLogin)/qna/_api/postPosts";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";

export default function PostTable() {
    const router = useRouter();
    const [postData, setPostData] = useState<Post[]>([]);
    const [totalPage, setTotalPage] = useState<number>(4);
    const [search, setSearch] = useState<Query>({sort_by:"create_at", asc: false, size:14, page:1});
    const [selectOption, setSelectOption] = useState<SelectBoxOption>({ table: "post", column: "title", name: "Title" });
    const [pageRange, setPageRange] = useState<{ start: number, end: number }>({ start: 1, end: 10 });

    const selectBoxOptions: SelectBoxOption[] = [
        { table: "post", column: "title", name: "Title" },
        { table: "post_category", column: "name", name: "Category" },
        { table: "user", column: "id", name: "ID" },
        { table: "user", column: "name", name: "Name" },
    ];

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return new Intl.DateTimeFormat("en-GB", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric"
        }).format(date);
    };

    const handlePageChange = (newPageNumber: number) => {
        setSearch(prevPage => ({
            ...prevPage,
            page: newPageNumber
        }));
        if (newPageNumber < pageRange.start || newPageNumber > pageRange.end) {
            const newStart = Math.floor((newPageNumber - 1) / 10) * 10 + 1;
            setPageRange({ start: newStart, end: newStart + 9 });
        }
    };

    const handleSearchChange = (option: SelectBoxOption, value: string) => {
        setSearch((prevSearch) => ({
            ...prevSearch,
            filter_groups:[
                {
                    condition_type: "OR",
                    filters: [
                        {
                            table: option.table!,
                            column: option.column!,
                            value: value,
                            operator: "LIKE"
                        }
                    ]
                }
            ],
            page:1
        }));
        setPageRange({ start: 1, end: 10 });
    };

    const handleRowClick = async (post: Post, userId: string) => {
        //if (session?.user. === 'USER') await fetchPostId(post.id!, false);
        router.push(`/qna/${userId}/${post.id}`);
    };

    const qnaButtonClick = () => {
        router.push('/qna/question');
    }

    const renderPageNumbers = () => {
        const pageNumbers = [];
        for (let i = pageRange.start; i <= pageRange.end && i <= totalPage; i++) {
            pageNumbers.push(
                <button
                    key={i}
                    className={i === search.page ? style.activePage : style.deActivePage}
                    onClick={() => handlePageChange(i)}
                >
                    {i}
                </button>
            );
        }
        return pageNumbers;
    };

    const fetchData = useCallback(async (search: Query) => {
        const response = await postPosts(search);
        const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
        const responseData = await response.json();
        setPostData(responseData as Post[]);
        setTotalPage(totalPage);
    }, []);

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <>
            <section className={style.filterContainer}>
                <div className={style.filterContainerLeft}>
                    <SelectBox
                        value={selectOption.name}
                        options={selectBoxOptions}
                        label={" "}
                        onChange={(selectedOption) =>{
                            setSelectOption(selectedOption);
                        }}
                    />
                </div>
                <div className={style.filterContainerRight}>
                    <InputBox onChange={(value) => {
                        handleSearchChange(selectOption, value)
                    }}></InputBox>
                </div>
            </section>
            <section className={style.tableContainer}>
                <table className={style.table}>
                    <thead>
                    <tr>
                        <th className={style.state}>State</th>
                        <th className={style.category}>Category</th>
                        <th className={style.title}>Title</th>
                        <th/>
                        <th className={style.user}>User Name</th>
                        <th className={style.date}>Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    {postData && postData.length > 0 && postData.map((row, rowIndex) => (
                        <tr key={rowIndex} onClick={() => handleRowClick(row, row.user?.id!)}>
                            <td>{row.read ? 'Finished' : 'To Be Confirmed'}</td>
                            <td>
                                <span className={`${style.category} ${style[`category-${row.post_category!.name}`]}`}>
                                    {row.post_category!.name}
                                </span>
                            </td>
                            <td className={style.titleTd}>
                                {row.title}
                            </td>
                            <td className={style.newAndComment}>
                                <FontAwesomeIcon className={style.commentIcon} icon={faComment}/>
                                {/*{row.comments && row.comments.length > 0 ? row.comments.length : 0}
                                {row.read && session?.user.role === 'USER' && <span className={style.new}>New</span>}*/}
                            </td>
                            <td>{row.user?.name}</td>
                            <td>{formatDate(row.create_at!!)}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
                <button className={style.addButton} onClick={qnaButtonClick}>
                    QnA
                </button>
                <div className={style.pagination}>
                    <button
                        className={style.paginationAngle}
                        disabled={search.page === 1}
                        onClick={() => handlePageChange((search.page ?? 1) - 1)}
                    ><FontAwesomeIcon icon={faAngleLeft}/>
                    </button>
                    {renderPageNumbers()}
                    <button
                        className={style.paginationAngle}
                        disabled={search.page === totalPage}
                        onClick={() => handlePageChange((search.page ?? 1) + 1)}
                    ><FontAwesomeIcon icon={faAngleRight}/>
                    </button>
                </div>
            </section>
        </>
    );
}