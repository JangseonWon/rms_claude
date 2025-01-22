"use client"

import * as React from "react";
import {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/manager/_component/orderDeletePage.module.css";
import globalTableStyle from "@/css/globalTable.module.css";
import requestStyle from "@/css/order/requestTable.module.css";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import {Query} from "@/model/Query";
import SelectBox from "@/app/_component/SelectBox";
import InputBox from "@/app/_component/InputBox";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {Status} from "@/model/Status";
import {postRequests} from "@/app/(afterLogin)/request/order/_api/postRequests";
import type {Request} from "@/model/Request";
import BlueButton from "@/app/_component/BlueButton";
import {deleteOrder} from "@/app/(afterLogin)/manager/_api/deleteOrder";
import {format} from "date-fns";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

interface RequestWithSelected extends Request {
    isSelected?: boolean;
}

const selectBoxOptions: SelectBoxOption[] = [
    { table: "user", column: "id", name: "User ID" },
    { table: "user", column: "name", name: "User Name" },
    { table: "organization", column: "name", name: "Institution" },
    { table: "sample", column: "barcode", name: "Registration ID" },
    { table: "service", column: "name", name: "Service" },
    { table: "request", column: "status", name: "Status"},
    { table: "patient", column: "name", name: "Patient(s) Name" },
    { table: "patient", column: "serial", name: "MRN" }
];
const defaultSearch: Query = {size:10, page:1}

export default function OrderDeletePage() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] = useState<Query>(defaultSearch);
    const isSelectedAll = requestData.length > 0 && requestData.every((row) => row.isSelected);
    const showAlert = CallAlertDialog();

    const handleOnClickOrderDelete = async () => {
        const selectedRequests = requestData.filter(request => request.isSelected);
        if( selectedRequests.length === 0) {
            showAlert("No selected.");
            return;
        }
        const response = await deleteOrder(selectedRequests)
        if(response.ok) showAlert("deleted!")
        else showAlert("fail");
        fetchData(search);
    }

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
                                table: "request",
                                column: "create_at",
                                value: format(from, "yyyy-MM-dd"),
                                operator: ">="
                            },
                            {
                                table: "request",
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
                            newFilter,
                            ...([{
                                table: "request",
                                column: "status",
                                operator: "!=",
                                value: Status.TOTAL.valueOf()
                            }])
                        ]
                    },
                    ...(prevSearch.filter_groups || []).filter(group => group.filters?.some(filter => filter.column === "create_at"))
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

    const handleSelectAll = (isSelected: boolean) => {
        setRequestData((prevData) =>
            prevData.map((row) => ({ ...row, isSelected }))
        );
    };

    const handleSelectChange = (rowIndex: number, isSelected: boolean) => {
        setRequestData((prevData) => {
            const updatedData = [...prevData];
            updatedData[rowIndex].isSelected = isSelected;
            return updatedData;
        });
    };

    const fetchData = async (search: Query) => {
        try {
            const response = await postRequests(search);
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const data = await response.json();
            setRequestData(data as Request[]);
            setTotalPage(totalPage);
        }
        catch {
            setRequestData([]);
        }
    };

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <>
            <div className={style.container}>
                <div className={style.header}>
                    Order Delete Page
                </div>
                <div className={style.buttonSection}>
                    <BlueButton name={"DELETE"} onClick={handleOnClickOrderDelete}/>
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
                                    <th>
                                        <label form="agree" className={style.checkbox}>
                                            <input
                                                type="checkbox"
                                                checked={isSelectedAll}
                                                onChange={() => handleSelectAll(!isSelectedAll)}
                                                className={style.checkbox}
                                            />
                                            <span className={style.checkmark}></span>
                                        </label>
                                    </th>
                                    <th className={requestStyle.longColumn}>Order Date<br/>(DD-MM-YYYY)</th>
                                    <th>User ID</th>
                                    <th>User Name</th>
                                    <th>Institution</th>
                                    <th>Registration ID</th>
                                    <th>Service</th>
                                    <th>Status</th>
                                    <th>Patient(s) Name</th>
                                    <th>MRN</th>
                                </tr>
                                </thead>
                                <tbody>
                                {requestData && requestData.length > 0 && requestData.map((request, rowIndex) => (
                                    <tr key={`${request.service!.id}${request.sample!.id}`}>
                                        <td onClick={(e) => e.stopPropagation()}>
                                            <label form="agree" className={style.checkbox}>
                                                <input
                                                    type="checkbox"
                                                    checked={request.isSelected || false}
                                                    onChange={() => handleSelectChange(rowIndex, !request.isSelected)}
                                                    className={style.checkbox}
                                                />
                                                <span className={style.checkmark}></span>
                                            </label>
                                        </td>
                                        <td>{request.create_at ? new Date(request.create_at).toLocaleDateString('en-GB').replace(/\//g, '-') : ''}</td>
                                        <td>{request.user?.id}</td>
                                        <td>{request.user?.name}</td>
                                        <td>{request.sample?.patient?.organization?.name}</td>
                                        <td>{request.sample?.barcode}</td>
                                        <td>{request.service?.name}</td>
                                        <td>{request.status}</td>
                                        <td>{request.sample?.patient?.name}</td>
                                        <td>{request.sample?.patient?.serial}</td>
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
