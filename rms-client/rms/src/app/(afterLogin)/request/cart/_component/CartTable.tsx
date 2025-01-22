"use client"

import globalTableStyle from "@/css/globalTable.module.css";
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
import CellTooltip from "@/app/_component/CellToolTip";

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
            return format(date, "dd-MM-yyyy");
        }
        return "-";
    };

    return (
        <div className={globalTableStyle.container}>
            <section>
                <div className={globalTableStyle.formGroupRight}>
                    <GreenButton name={"Delete"} onClick={handleDeleteCart}/>
                    <BlueButton name={"Save & Order"} onClick={handleCartToOrder}/>
                </div>
                <div className={globalTableStyle.formGroupRight}>
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
            <div className={globalTableStyle.tableContainer}>
                <table className={globalTableStyle.table}>
                    <thead>
                    <tr>
                        <th>
                            <label form="agree" className={globalTableStyle.checkbox}>
                                <input
                                    type="checkbox"
                                    checked={isSelectedAll}
                                    onChange={() => handleSelectAll(!isSelectedAll)}
                                    className={globalTableStyle.checkbox}
                                />
                                <span className={globalTableStyle.checkmark}></span>
                            </label>
                        </th>
                        <th className={globalTableStyle.middleColumn}>Institution</th>
                        <th className={globalTableStyle.longColumn}>Patient(s) Name</th>
                        <th className={globalTableStyle.longColumn}>Service</th>
                        <th className={globalTableStyle.middleColumn}>Patient BOD<br/>(DD-MM-YYYY)</th>
                        <th className={globalTableStyle.shortColumn}>Gender</th>
                        <th className={globalTableStyle.longColumn}>MRN</th>
                        <th className={globalTableStyle.middleColumn}>Collection Date<br/>(DD-MM-YYYY)</th>
                        <th className={globalTableStyle.shortColumn}>Info</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requestData && requestData.length > 0 ? ( requestData.map((request, rowIndex) => (
                            <tr key={`${request.service!.id}${request.sample!.id}`}>
                                <td onClick={(e) => e.stopPropagation()}>
                                    <label form="agree" className={globalTableStyle.checkbox}>
                                        <input
                                            type="checkbox"
                                            checked={request.isSelected || false}
                                            onChange={() => handleSelectChange(rowIndex, !request.isSelected)}
                                            className={globalTableStyle.checkbox}
                                        />
                                        <span className={globalTableStyle.checkmark}></span>
                                    </label>
                                </td>
                                <td className={globalTableStyle.middleColumn}><CellTooltip text={request.sample!.patient!.organization!.id}/></td>
                                <td className={globalTableStyle.longColumn}><CellTooltip text={request.sample!.patient!.name}/></td>
                                <td className={globalTableStyle.longColumn}><CellTooltip text={request.service!.name}/></td>
                                <td className={globalTableStyle.middleColumn}>{request.sample?.patient ?
                                    formatDate(request.sample.patient.birth_year, request.sample.patient.birth_month, request.sample.patient.birth_day) : '-'}
                                </td>
                                <td className={globalTableStyle.shortColumn}>{request.sample!.patient!.sex}</td>
                                <td className={globalTableStyle.longColumn}>{request.sample!.patient!.serial}</td>
                                <td className={globalTableStyle.middleColumn}>{request.sample?.sampling_on! ? format(new Date(request.sample?.sampling_on!), "dd-MM-yyyy") : '-'}</td>
                                <td className={globalTableStyle.shortColumn}>
                                    <FontAwesomeIcon
                                        icon={faFileLines}
                                        className={globalTableStyle.info}
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            handleInfoClick(request)
                                        }}/>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan={10} className={globalTableStyle.noData}>
                                The searched data does not exist
                            </td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
            <div className={globalTableStyle.pagination}>
                <span>items per page:</span>
                <div className={globalTableStyle.select}>
                    <select onChange={handlePageSizeChange}>
                        <option value="10">10</option>
                        <option value="20">20</option>
                        <option value="50">50</option>
                    </select>
                </div>
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