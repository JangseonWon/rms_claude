"use client"

import * as React from "react";
import {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/manager/_component/managePage.module.css";
import globalTableStyle from "@/css/globalTable.module.css";
import requestStyle from "@/css/order/requestTable.module.css";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import {Query} from "@/model/Query";
import SelectBox from "@/app/_component/SelectBox";
import InputBox from "@/app/_component/InputBox";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {format} from "date-fns";
import {UserHistory} from "@/model/UserHistory";
import {postUserHistory} from "@/app/(afterLogin)/manager/_api/postUserHistory";

const selectBoxOptions: SelectBoxOption[] = [
    { table: "user_history", column: "field_name", name: "Field Name" },
    { table: "user_history", column: "changed_by", name: "Changed By" },
    { table: "user_history", column: "new_value", name: "New Value" },
    { table: "user_history", column: "old_value", name: "Old Value" },
    { table: "user_history", column: "user_id", name: "User ID" }
];
const defaultSearch: Query = {
    sorts: [
        {
            "table": "user_history",
            "column": "id",
            "asc": false
        }
    ],
    size:10,
    page:1,
    sort_by: "changed_at"
}

export default function UserHistoryPage() {
    const [userHistoryData, setUserHistoryData] = useState<UserHistory[]>([]);
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] = useState<Query>(defaultSearch);

    const addDateFilter = (from: Date | null, to: Date | null) => {
        if (!from || !to) return;

        setSearch((prevSearch) => {
            const updatedFilters = (prevSearch.filter_groups || []).filter(group =>
                !group.filters?.some(filter => filter.column === "changed_at")
            ) || [];

            return {
                ...prevSearch,
                filter_groups: [
                    ...updatedFilters,
                    {
                        condition_type: "AND",
                        filters: [
                            {
                                table: "user_history",
                                column: "changed_at",
                                value: format(from, "yyyy-MM-dd"),
                                operator: ">="
                            },
                            {
                                table: "user_history",
                                column: "changed_at",
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

    const handleSearchChange = (option: SelectBoxOption, value: string) => {
        setSearch((prevSearch) => {
            const newFilter = {
                table: option.table!,
                column: option.column!,
                value: value,
                operator: "LIKE"
            };

            return {
                ...prevSearch,
                filter_groups: [
                    {
                        condition_type: "AND",
                        filters: [
                            newFilter
                        ]
                    },
                    ...(prevSearch.filter_groups || []).filter(group => group.filters?.some(filter => filter.column === "changed_at"))
                ],
                page: 1
            };
        });
    };

    const handlePageChange = (newPageNumber: number) => {
        setSearch(prevPage =>({
            ...prevPage,
            page: newPageNumber
        }));
    };

    const fetchData = async (search: Query) => {
        try {
            const response = await postUserHistory(search);
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const data = await response.json();
            setUserHistoryData(data as UserHistory[]);
            setTotalPage(totalPage);
        }
        catch {
            setUserHistoryData([]);
        }
    };

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <>
            <div className={style.container}>
                <div className={style.header}>
                    User History Page
                </div>
                <section className={style.section}>
                    <div className={globalTableStyle.container}>
                        <div className={globalTableStyle.formGroupBetween}>
                            <div>
                                <DatePickerRangeBox
                                    label={"from-to"}
                                    onChange={(from, to) =>{
                                        addDateFilter(from, to);
                                    }}
                                />
                            </div>
                            <div>
                                <SelectBox
                                    width={"200px"}
                                    value={selectedOption.name}
                                    options={selectBoxOptions}
                                    label={"filter"}
                                    onChange={(option) => {
                                        setSelectedOption(option);
                                    }}
                                />
                                <InputBox label={"search"} onChange={(value) => {
                                    handleSearchChange(selectedOption, value)
                                }}></InputBox>
                            </div>
                        </div>
                        <div className={style.tableContainer}>
                            <table className={requestStyle.table}>
                                <thead>
                                <tr>
                                    <th>Changed Date</th>
                                    <th>Changed By</th>
                                    <th>Field Name</th>
                                    <th>New Value</th>
                                    <th>Old Value</th>
                                    <th>User ID</th>
                                </tr>
                                </thead>
                                <tbody>
                                {userHistoryData && userHistoryData.length > 0 && userHistoryData.map((history) => (
                                    <tr key={`${history.id}`}>
                                        <td>{history.changed_at ? new Date(history.changed_at).toLocaleDateString().replace(/\//g, '-') : ''}</td>
                                        <td>{history.changed_by}</td>
                                        <td>{history.field_name}</td>
                                        <td>{history.new_value}</td>
                                        <td>{history.old_value}</td>
                                        <td>{history.user_id}</td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                        <div className={globalTableStyle.pagination}>
                            <span> 1-{totalPage} of {search.page} </span>
                            <button
                                className={globalTableStyle.pageButton}
                                disabled={search.page === 1}
                                onClick={() => handlePageChange((search.page ?? 1) - 1)}
                            ><FontAwesomeIcon icon={faAngleLeft}/>
                            </button>
                            <button
                                className={globalTableStyle.pageButton}
                                disabled={search.page === totalPage}
                                onClick={() => handlePageChange((search.page ?? 1) + 1)}
                            ><FontAwesomeIcon icon={faAngleRight}/>
                            </button>
                        </div>
                    </div>
                </section>
            </div>
        </>
    );
}
