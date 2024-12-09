"use client"

import style from "@/app/(afterLogin)/manager/_component/orderDeletePage.module.css";
import * as React from "react";
import {useEffect, useState} from "react";
import globalTableStyle from "@/css/globalTable.module.css";
import DatePickerRangeBox from "@/app/_component/DatePickerRangeBox";
import {FilterGroup, Query} from "@/model/Query";
import SelectBox from "@/app/_component/SelectBox";
import {Filter} from "@/model/Filter";
import InputBox from "@/app/_component/InputBox";
import requestStyle from "@/css/order/requestTable.module.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {Status} from "@/model/Status";
import {postRequests} from "@/app/(afterLogin)/request/order/_api/postRequests";
import type {Request} from "@/model/Request";
import BlueButton from "@/app/_component/BlueButton";

interface RequestWithSelected extends Request {
    isSelected?: boolean;
}

const selectBoxOptions: SelectBoxOption[] = [
    { table: "organization", column: "name", name: "Institution" },
    { table: "patient", column: "name", name: "Patient(s) Name" },
    { table: "service", column: "name", name: "Service" },
    { table: "patient", column: "sex", name: "Gender" },
    { table: "patient", column: "serial", name: "MRN" },
];
const defaultSearch: Query = {size:10, page:1}
const defaultFilter: Filter = {
    table: "request",
    column: "status",
    operator: "=",
    value: Status.COMPLETED_ORDER.valueOf()
}

export default function OrderDeletePage() {
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([]);
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] = useState<Query>(defaultSearch);
    const [searchFilter, setSearchFilter] = useState<Filter | undefined>(undefined);
    const [orderDateFilter, setOrderDateFilter] = useState<FilterGroup>()
    const isSelectedAll = requestData.length > 0 && requestData.every((row) => row.isSelected);
    const handleOnClickOrderDelete = async () => {

    }

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
        const updatedSearch = {
            ...search,
            filter_groups: [
                ...(orderDateFilter ? [orderDateFilter] : []),
                {
                    filters: [
                        defaultFilter,
                        ...(searchFilter ? [searchFilter] : []),
                    ],
                },
            ],
        };
        fetchData(updatedSearch);
    }, [search,searchFilter,orderDateFilter]);

    return (
        <>
            <div className={style.container}>
                <div className={style.header}>
                    Order Delete Page
                </div>
                <div className={style.buttonSection}>
                    <BlueButton name={"DELETE"}/>
                </div>
                <section className={style.section}>
                    <div className={globalTableStyle.container}>
                        <div className={globalTableStyle.formGroupBetween}>
                            <div>
                                <DatePickerRangeBox
                                    label={"from-to"}
                                    onChange={(from, to) => {
                                        setOrderDateFilter(
                                            from && to ? {
                                                filters: [
                                                    {
                                                        table: "request",
                                                        column: "create_at",
                                                        value: from?.toLocaleDateString('en-CA'),
                                                        operator: ">="
                                                    },
                                                    {
                                                        table: "request",
                                                        column: "create_at",
                                                        value: to.toLocaleDateString('en-CA'),
                                                        operator: "<="
                                                    }
                                                ]
                                            } as FilterGroup : undefined
                                        )
                                    }}/>
                            </div>
                            <div>
                                <SelectBox
                                    width={"200px"}
                                    value={selectedOption.name}
                                    options={selectBoxOptions}
                                    label={"filter"}
                                    onChange={(option) => {
                                        setSelectedOption(option);
                                        setSearchFilter({
                                            table: option.table!,
                                            column: option.column!
                                        } as Filter)
                                    }}
                                />
                                <InputBox label={"search"} onChange={(value) => {
                                    setSearchFilter(
                                        value && value.trim() !== ""
                                            ? {
                                                table: selectedOption.table,
                                                column: selectedOption.column,
                                                operator: "LIKE",
                                                value: value
                                            } as Filter
                                            : undefined
                                    );
                                }}></InputBox>
                            </div>
                        </div>
                        <div className={globalTableStyle.tableContainer}>
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
                                    <th>Global courier</th>
                                    <th className={requestStyle.middleColumn}>AirWaybill no.</th>
                                    <th>User Name</th>
                                    <th>Institution</th>
                                    <th>Registration ID</th>
                                    <th>Service</th>
                                    <th>Patient(s) Name</th>
                                    <th>MRN</th>
                                </tr>
                                </thead>
                                <tbody>
                                {requestData && requestData.length > 0 && requestData.map((request, rowIndex) => (
                                    <tr key={request.order_id! + request.service!.id + request.sample!.id}>
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
                                        <td>{request.courier_company}</td>
                                        <td>{request.awb_number}</td>
                                        <td>{request.order?.user?.name}</td>
                                        <td>{request.sample?.patient?.organization?.name}</td>
                                        <td>{request.sample?.barcode}</td>
                                        <td>{request.service?.name}</td>
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
