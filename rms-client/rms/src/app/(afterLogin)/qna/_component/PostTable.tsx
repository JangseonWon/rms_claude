"use client"

import React, {useState} from "react";
import style from "./postTable.module.css";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {Paging} from "@/model/Paging";
import {Post} from "@/model/Post";
import {useRouter} from "next/navigation";
import {faComment} from "@fortawesome/free-regular-svg-icons";

export default function PostTable() {
    const postTestData: Post[] = [
        {id: '6e521e46-0686-40b2-a190-9270e69a059e', create_at: '2024-01-01', last_modify_at: '2024-01-01', content: '', title: 'Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10', user_id: 'John Doe', category_id: 'update', read: false, comment: [{id:'1'},{id:'1'},{id:'1'},{id:'1'}]},
        {id: '40fa0256-0784-4272-81fe-2cb32a2c6686', create_at: '2024-01-02', last_modify_at: '2024-01-02', content: '', title: 'Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10', user_id: 'Jane Smith', category_id: 'bug', read: false, comment: [{id:'1'}, {id:'1'}, {id:'1'}]},
        {id: '49adc8e4-0dd2-4ca0-a174-eb8515c6d616', create_at: '2024-01-03', last_modify_at: '2024-01-03', content: '', title: 'Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10', user_id: 'Alice Johnson', category_id: 'question', read: true, comment: [{id:'1'}, {id:'1'}]},
        {id: '383f3665-7bf0-489e-979f-06911a57656b', create_at: '2024-01-04', last_modify_at: '2024-01-04', content: '', title: 'Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10', user_id: 'Bob Brown', category_id: 'question', read: false, comment: [{id:'1'}, {id:'1'}, {id:'1'}, {id:'1'}]},
        {id: '2f008a39-e9a8-40d3-81d0-f026bf0c297c', create_at: '2024-01-05', last_modify_at: '2024-01-05', content: '', title: 'Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10', user_id: 'Charlie White', category_id: 'question', read: false, comment: [{id:'1'}, {id:'1'}]},
        {id: '9b0c50bf-8153-4052-b8c2-1e42f893f8ab', create_at: '2024-01-06', last_modify_at: '2024-01-06', content: '', title: 'Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10', user_id: 'David Green', category_id: 'bug', read: false, comment: []},
        {id: '2ec25ff8-b7bc-49a2-8382-88c1d482b20e', create_at: '2024-01-07', last_modify_at: '2024-01-07', content: '', title: 'Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10', user_id: 'Eve Black', category_id: 'question', read: false, comment: [{id:'1'}, {id:'1'}]},
        {id: '4e8f9aaf-1d8f-4b32-a99f-ddaad1d15ade', create_at: '2024-01-08', last_modify_at: '2024-01-08', content: '', title: 'Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10', user_id: 'Frank Blue', category_id: 'question', read: false, comment: [{id:'1'}, {id:'1'}, {id:'1'}, {id:'1'}, {id:'1'}]},
        {id: '66b52543-0709-459d-b691-5a66a02fbce6', create_at: '2024-01-09', last_modify_at: '2024-01-09', content: '', title: 'Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10', user_id: 'Grace Red', category_id: 'question', read: false, comment: [{id:'1'}, {id:'1'}]},
        {id: '0acb89e5-7950-4242-8a62-856887d91272', create_at: '2024-01-10', last_modify_at: '2024-01-10', content: '', title: 'Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10', user_id: 'Hank Yellow', category_id: 'question', read: true, comment: [{id:'1'}]},
        {id: '6e521e46-0686-40b2-a190-9270e69a059e', create_at: '2024-01-01', last_modify_at: '2024-01-01', content: '', title: 'Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10', user_id: 'John Doe', category_id: 'update', read: false, comment: [{id:'1'}]},
        {id: '40fa0256-0784-4272-81fe-2cb32a2c6686', create_at: '2024-01-02', last_modify_at: '2024-01-02', content: '', title: 'Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10', user_id: 'Jane Smith', category_id: 'bug', read: false, comment: [{id:'1'}, {id:'1'}, {id:'1'}]},
        {id: '49adc8e4-0dd2-4ca0-a174-eb8515c6d616', create_at: '2024-01-03', last_modify_at: '2024-01-03', content: '', title: 'Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10', user_id: 'Alice Johnson', category_id: 'question', read: false, comment: [{id:'1'}]},
        {id: '383f3665-7bf0-489e-979f-06911a57656b', create_at: '2024-01-04', last_modify_at: '2024-01-04', content: '', title: 'Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10 Test Post 10', user_id: 'Bob Brown', category_id: 'question', read: false, comment: [{id:'1'}]},
    ];

    const router = useRouter();
    const [postData, setPostData] = useState<Post[]>([]);
    const [totalPage, setTotalPage] = useState<number>(4);
    const [search, setSearch] =
        useState<Paging>({filters: [], sort_by:"date", asc: true, size:14, page:1});
    const [searchKey, setSearchKey] = useState<string>("service_id");
    const [searchValue, setSearchValue] = useState<string>("");

    const handlePageChange = (newPageNumber: number) => {
        setSearch(prevPage =>({
            ...prevPage,
            page: newPageNumber
        }));
    };

    const handleSearchChange = (newFilter: { key: string; value: string }) => {
        const filterWithOperator = { ...newFilter, operator: "LIKE" };
        setSearch((prevSearch) => ({
            ...prevSearch,
            filters: [filterWithOperator],
            page:1
        }));
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
        for (let i = 1; i <= totalPage; i++) {
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
                        <th className={style.category}>Category</th>
                        <th className={style.title}>Title</th>
                        <th/>
                        <th className={style.user}>User</th>
                        <th className={style.date}>Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    {postTestData && postTestData.length > 0 && postTestData.map((row, rowIndex) => (
                        <tr key={rowIndex} onClick={() => handleRowClick(row, row.user_id)}>
                            <td>
                                <span
                                className={`${style.category} ${style[`category-${row.category_id}`]}`}>{row.category_id}
                                </span>
                            </td>
                            <td className={style.titleTd}>
                                {row.title}
                            </td>
                            <td className={style.newAndComment}>
                                <FontAwesomeIcon className={style.commentIcon} icon={faComment}/>
                                {row.comment.length}
                                {row.read && <span className={style.new}>New</span>}
                            </td>
                            <td>{row.user_id}</td>
                            <td>{row.create_at}</td>
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