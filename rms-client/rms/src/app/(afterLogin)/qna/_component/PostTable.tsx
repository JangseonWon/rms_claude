"use client"

import React, {useCallback, useEffect, useState} from "react";
import style from "./postTable.module.css";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {Paging} from "@/model/Paging";
import {Post} from "@/model/Post";
import {useRouter} from "next/navigation";
import {faComment} from "@fortawesome/free-regular-svg-icons";
import {getPostSearch} from "@/app/(afterLogin)/qna/_api/getPostSearch";
import {useSession} from "next-auth/react";

const categoryMap: { [key: string]: string } = {
    "f9476263-f8b2-4ff9-b5f9-ed680715401e": "Service",
    "7cefa58c-d85b-4f9e-82ff-dc46ba953e27": "Bug",
    "f1d0a814-8c90-4103-bbd5-6c0e54d37808": "Result",
    "f86a9106-e431-4649-a4f9-c61e73ec7bab": "Others",
};

export default function PostTable() {

    const router = useRouter();
    const [postData, setPostData] = useState<Post[]>([]);
    const [totalPage, setTotalPage] = useState<number>(4);
    const [search, setSearch] =
        useState<Paging>({filters: [], sort_by:"create_at", asc: false, size:14, page:1});
    const [searchKey, setSearchKey] = useState<string>("title");
    const [searchValue, setSearchValue] = useState<string>("");
    const [pageRange, setPageRange] = useState<{ start: number, end: number }>({ start: 1, end: 10 });
    const { data: session } = useSession();

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

    const handleSearchChange = (newFilter: { key: string; value: string }) => {
        const filterWithOperator = { ...newFilter, operator: "LIKE" };
        setSearch((prevSearch) => ({
            ...prevSearch,
            filters: [filterWithOperator],
            page:1
        }));
        setPageRange({ start: 1, end: 10 });
    };

    const handleSearchKeyChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const key = event.target.value;
        setSearchKey(key);
        handleSearchChange({key: key, value: searchValue});
    };

    const handleRowClick = (post: Post, userId: string) => {
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

    const fetchData = useCallback(async (search: Paging) => {
        const response = await getPostSearch(search);
        const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
        const responseData = await response.json();
        const data = responseData.data;

        setPostData(data as Post[]);
        setTotalPage(totalPage);
    }, []);

    useEffect(() => {
        setPostData([]);
        fetchData(search)
    }, [search]);

    return (
        <>
            <section className={style.filterContainer}>
                <div className={style.filterContainerLeft}>
                    <select className={style.selectSearchKey} onChange={handleSearchKeyChange}>
                        <option value="title">Title</option>
                        <option value="user_id">User</option>
                    </select>
                </div>
                <div className={style.filterContainerRight}>
                    <InputBox label={"search"} onChange={(value) => {
                        setSearchValue(value);
                        handleSearchChange({key: searchKey, value: value})
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
                            <td>{row.read ? 'To Be Confirmed' : 'Finished'}</td>
                            <td>
                                <span
                                    className={`${style.category} ${style[`category-${categoryMap[row.post_category_id!!]}`]}`}
                                >
                                    {categoryMap[row.post_category_id!!] || row.post_category_id}
                                </span>
                            </td>
                            <td className={style.titleTd}>
                                {row.title}
                            </td>
                            <td className={style.newAndComment}>
                                <FontAwesomeIcon className={style.commentIcon} icon={faComment}/>
                                {row.comments && row.comments.length > 0 ? row.comments.length : 0}
                                {row.read && session?.user.role === 'USER' && <span className={style.new}>New</span>}
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
                        onClick={() => handlePageChange(search.page - 1)}
                    ><FontAwesomeIcon icon={faAngleLeft}/>
                    </button>
                    {renderPageNumbers()}
                    <button
                        className={style.paginationAngle}
                        disabled={search.page === totalPage}
                        onClick={() => handlePageChange(search.page + 1)}
                    ><FontAwesomeIcon icon={faAngleRight}/>
                    </button>
                </div>
            </section>
        </>
    );
}