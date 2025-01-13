"use client"

import style from "@/css/globalTable.module.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import React, {useEffect, useState} from "react";
import type {Request} from "@/model/Request";
import {format} from "date-fns";
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";
import {deleteRequest} from "@/app/(afterLogin)/request/cart/_api/deleteRequest";
import {putRequest} from "@/app/(afterLogin)/request/cart/_api/putRequest";
import {faFileLines} from "@fortawesome/free-regular-svg-icons/faFileLines";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {Query} from "@/model/Query";
import {searchRequests} from "@/app/(afterLogin)/request/cart/_api/searchRequests";
import {Filter} from "@/model/Filter";
import SelectBox from "@/app/_component/SelectBox";
import InputBox from "@/app/_component/InputBox";
import CartInfo from "@/app/(afterLogin)/request/cart/_component/CartInfo";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

interface RequestWithSelected extends Request {
    isSelected?: boolean;
}

const selectBoxOptions: SelectBoxOption[] = [
    { table: "service", column: "name", name: "Service Name" },
    { table: "sample", column: "barcode", name: "Registration ID" },
    { table: "organization", column: "id", name: "Institution" },
    { table: "patient", column: "name", name: "Patient(s) Name" },
    { table: "patient", column: "serial", name: "MRN" },
    { table: "patient", column: "birth_year", name: "Patient BOD" },
    { table: "request", column: "report_at", name: "Report Date" }
];

export default function CartTable() {
    const showAlert = CallAlertDialog();
    const [requestData, setRequestData] = useState<RequestWithSelected[]>([])
    const [search, setSearch] = useState<Query>({asc: false, size:10, page:1});
    const [totalPage, setTotalPage] = useState<number>();
    const [pageRange, setPageRange] = useState<{ start: number, end: number }>({ start: 1, end: 5 });
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const isSelectedAll = requestData.length > 0 && requestData.every((row) => row.isSelected);
    const [modalOpen, setModalOpen] = useState<boolean>(false);
    const [infoRequest, setInfoRequest] = useState<Request>();
    const [filter, setFilter] = useState<Filter>({
        table: selectBoxOptions[0].table!,
        column: selectBoxOptions[0].column!,
        operator: "LIKE",
        value: ""
    })

    useEffect(() => {
        setSearch((prevSearch) => ({
            ...prevSearch,
            filter_groups: [
                {
                    ...prevSearch?.filter_groups?.[0] || {},
                    filters: [
                        filter,
                    ],
                },
            ],
        }));
    }, [filter]);

    useEffect(() => {
        fetchData(search)
    }, [search]);


    const fetchData = async (search: Query) => {
        try {
            const response = await searchRequests(search);
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setRequestData(responseData as Request[]);
            setTotalPage(totalPage);
        } catch (error) {
            setRequestData([]);
        }
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

    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newSize = parseInt(event.target.value);
        setSearch(prevSearch => ({
            ...prevSearch,
            size: newSize,
            page: 1
        }));
    };

    const handleSelectChange = (rowIndex: number, isSelected: boolean) => {
        setRequestData((prevData) => {
            const updatedData = [...prevData];
            updatedData[rowIndex].isSelected = isSelected;
            return updatedData;
        });
    };
    const handleSelectAll = (isSelected: boolean) => {
        setRequestData((prevData) =>
            prevData.map((row) => ({ ...row, isSelected }))
        );
    };

    const handleInfoClick = (row: RequestWithSelected) => {
        setInfoRequest(row);
        setModalOpen(true);
    };

    const closeModal = () => {
        setInfoRequest(undefined);
        setModalOpen(false);
        fetchData(search);
    }

    const handleDeleteCart = async () => {
        const selectedRequests = requestData.filter(request => request.isSelected);
        if( selectedRequests.length === 0) {
            showAlert("No selected.");
            return;
        }
        const response = await deleteRequest(selectedRequests)
        if(response.ok) showAlert("deleted!");
        else showAlert("fail");
        fetchData(search);
    };

    const handleCartToOrder = async () => {
        const selectedRequests = requestData.filter(request => request.isSelected);
        const response = await putRequest(selectedRequests)
        if(response.ok) showAlert("ordered!");
        else showAlert("fail");
        fetchData(search);
    };

    const formatDate = (year: number | undefined, month: number | undefined, day: number | undefined) => {
        if (year !== undefined && month !== undefined && day !== undefined) {
            const date = new Date(year, month - 1, day);
            return format(date, "dd-MMM-yyyy");
        }
        return "-";
    };

    return (
        <div className={style.container}>
            <section>
                <div className={style.topFirstSection}>
                    <GreenButton name={"Delete"} onClick={handleDeleteCart}/>
                    <BlueButton name={"Save & Order"} onClick={handleCartToOrder}/>
                </div>
                <div className={style.topSecondSection}>
                    <SelectBox
                        width={"200px"}
                        value={selectedOption.name}
                        options={selectBoxOptions}
                        label={"filter"}
                        onChange={(option) => {
                            setSelectedOption(option);
                            setFilter(prev => ({
                                ...prev,
                                table: option.table!,
                                column: option.column!
                            }))
                        }}
                    />
                    <InputBox label={"search"} onChange={(value) => {
                        setFilter(prev => ({
                            ...prev,
                            value: value
                        }))
                    }}></InputBox>
                </div>
            </section>
            <div className={style.tableContainer}>
                <table className={style.table}>
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
                        <th className={style.shortColumn}>Institution</th>
                        <th className={style.shortColumn}>Patient(s) Name</th>
                        <th className={style.longColumn}>Service</th>
                        <th className={style.dateColumn}>Patient BOD<br/>(DD/MM/YYYY)</th>
                        <th>Gender</th>
                        <th className={style.longColumn}>MRN</th>
                        <th className={style.dateColumn}>Collection Date<br/>(DD/MM/YYYY)</th>
                        <th>Info</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requestData && requestData.length > 0 && requestData.map((row, rowIndex) => (
                        <tr key={row.order_id! + row.service!.id + row.sample!.id}>
                            <td onClick={(e) => e.stopPropagation()}>
                                <label form="agree" className={style.checkbox}>
                                    <input
                                        type="checkbox"
                                        checked={row.isSelected || false}
                                        onChange={() => handleSelectChange(rowIndex, !row.isSelected)}
                                        className={style.checkbox}
                                    />
                                    <span className={style.checkmark}></span>
                                </label>
                            </td>
                            <td>{row.sample!.patient!.organization!.id}</td>
                            <td>{row.sample!.patient!.name}</td>
                            <td>{row.service!.name}</td>
                            <td>{row.sample?.patient ?
                                formatDate(row.sample.patient.birth_year, row.sample.patient.birth_month, row.sample.patient.birth_day) : '-'}
                            </td>
                            <td>{row.sample!.patient!.sex}</td>
                            <td>{row.sample!.patient!.serial}</td>
                            <td>{row.sample?.sampling_on! ? format(new Date(row.sample?.sampling_on!), "dd-MMM-yyyy") : '-'}</td>
                            <td>
                                <FontAwesomeIcon
                                    icon={faFileLines}
                                    className={style.info}
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        handleInfoClick(row)
                                    }}/>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
            <div className={style.pagination}>
                <span>items per page:</span>
                <div className={style.select}>
                    <select onChange={handlePageSizeChange}>
                        <option value="10">10</option>
                        <option value="20">20</option>
                        <option value="50">50</option>
                    </select>
                </div>
                <span> 1-{totalPage} of {search.page} </span>
                <button
                    className={style.pageButton}
                    disabled={search.page === 1}
                    onClick={() => handlePageChange((search.page ?? 1) - 1)}
                ><FontAwesomeIcon icon={faAngleLeft}/>
                </button>
                <button
                    className={style.pageButton}
                    disabled={search.page === totalPage}
                    onClick={() => handlePageChange((search.page ?? 1) + 1)}
                ><FontAwesomeIcon icon={faAngleRight}/>
                </button>
            </div>
            {modalOpen && (
                <CartInfo
                    serviceId={infoRequest?.service!.id!}
                    sampleId={infoRequest?.sample!.id!}
                    userId={infoRequest?.user!.id!}
                    closeModal={closeModal}
                />
            )}
        </div>
    )
}