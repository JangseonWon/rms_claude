"use client"

import React, {useCallback, useEffect, useState} from "react";
import style from "@/css/qnaTable.module.css";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {Query} from "@/model/Query";
import {Post} from "@/model/Post";
import {useRouter} from "next/navigation";
import {faComment} from "@fortawesome/free-regular-svg-icons";
import {postSearchPosts} from "@/app/(afterLogin)/qna/_api/postSearchPosts";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import {format} from "date-fns";
import {useSession} from "next-auth/react";

export default function FaqTable() {
    const router = useRouter();
    const [postData, setPostData] = useState<Post[]>([]);
    const [totalPage, setTotalPage] = useState<number>(4);
    const [search, setSearch] = useState<Query>({sort_by:"create_at", asc: false, size:8, page:1});
    const [selectOption, setSelectOption] = useState<SelectBoxOption>({ table: "post", column: "title", name: "Title" });
    const [pageRange, setPageRange] = useState<{ start: number, end: number }>({ start: 1, end: 10 });
    const { data: session } = useSession();

    const selectBoxOptions: SelectBoxOption[] = [
        { table: "post", column: "title", name: "Title" },
        { table: "user", column: "id", name: "ID" },
        { table: "user", column: "name", name: "Name" },
    ];

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

    const addDateFilter = (from: Date | null, to: Date | null) => {
        if (!from || !to) return;

        setSearch((prevSearch) => {
            const updatedFilters = (prevSearch.filter_groups || []).filter(group =>
                !group.filters?.some(filter => filter.column === "create_at")
            ) || [];

            return {
                ...prevSearch,
                filter_groups: [
                    ...updatedFilters,
                    {
                        condition_type: "AND",
                        filters: [
                            {
                                table: "post",
                                column: "create_at",
                                value: format(from, "yyyy-MM-dd"),
                                operator: ">="
                            },
                            {
                                table: "post",
                                column: "create_at",
                                value: format(to, "yyyy-MM-dd"),
                                operator: "<="
                            }
                        ]
                    }
                ],
                page: 1
            };
        });
    };

    const handleRowClick = async (post: Post, userId: string) => {
        router.push(`/qna/faq/${userId}/${post.id}`);
    };

    const faqAddButtonClick = () => {
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
        const response = await postSearchPosts(search, 'faq');
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
                    <DatePickerRangeBox
                        label={"from-to"}
                        onChange={(from, to) =>{
                            addDateFilter(from, to);
                        }}/>
                </div>
                <div className={style.filterContainerRight}>
                    <SelectBox
                        value={selectOption.name}
                        options={selectBoxOptions}
                        label={"filter"}
                        onChange={(selectedOption) =>{
                            setSelectOption(selectedOption);
                        }}
                    />
                    <div className={style.search}>
                        <InputBox label={"search"} onChange={(value) => {
                            handleSearchChange(selectOption, value)
                        }}></InputBox>
                    </div>
                </div>
            </section>
            <section className={style.tableContainer}>
                <table className={style.table}>
                    <thead>
                    <tr>
                        <th className={style.category}>No</th>
                        <th className={style.title}>Title</th>
                        <th/>
                        <th className={style.user}>User Name</th>
                        <th className={style.date}>Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    {postData && postData.length > 0 && postData.map((row, rowIndex) => (
                        <tr key={rowIndex} onClick={() => handleRowClick(row, row.user?.id!)}>
                            <td>
                                {rowIndex + 1}
                            </td>
                            <td className={style.titleTd}>
                                {row.title}
                            </td>
                            <td className={style.newAndComment}>
                                <FontAwesomeIcon className={style.commentIcon} icon={faComment}/>
                            </td>
                            <td>{row.user?.name}</td>
                            <td>{row.create_at ? format(new Date(row.create_at), "dd-MMM-yyyy") : '-'}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
                {session?.user.role !== 'USER' && (
                    <button className={style.addButton} onClick={faqAddButtonClick}>
                        FAQ
                    </button>
                )}
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