'use client';

import React, {useEffect, useState} from "react";
import style from '@/css/modal.module.css';
import tableStyle from '@/css/globalTable.module.css';
import globalTableStyle from '@/css/globalTable.module.css';
import {faAngleLeft, faAngleRight, faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import BlueButton from "@/app/_component/BlueButton";
import {Request} from "@/model/Request";
import managementStyle from "@/css/managementTable.module.css";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {Query} from "@/model/Query";
import {useSetProbandRequest} from "@/app/(afterLogin)/request/services/[service]/single/store/useProbandStore";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import {postRequests} from "@/app/(afterLogin)/request/services/[service]/single/_api/postRequests";
import {format} from "date-fns";
import {useRequestStore} from "@/store/useRequestStore";
import {Filter} from "@/model/Filter";

type Props = {
    closeModal: () => void;
}

const selectBoxOptions: SelectBoxOption[] = [
    { table: "service", column: "name", name: "Service" },
    { table: "sample", column: "barcode", name: "Registration ID" },
    { table: "patient", column: "name", name: "Patient(s) Name" },
    { table: "patient", column: "serial", name: "MRN" }
];
export default function SearchProbandModal({ closeModal }: Props) {
    const {request} = useRequestStore()
    const defaultFilters: Filter[] = [
        {
            table: "organization",
            column: "id",
            value: request?.sample?.patient?.organization?.id!!,
            operator: "="
        },
        {
            table: "request_relation",
            column: "name",
            value: "ROOT",
            operator: "="
        },
        {
            table: "request",
            column: "status",
            value: "CART",
            operator: "!="
        }
    ]

    const [requests, setRequests] = useState<Request[]>([]);
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [search, setSearch] = useState<Query>({size:5, page:1});
    const [searchFilter, setSearchFilter] = useState<Filter | undefined>(undefined);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [selectedRequest, setSelectedRequest] = useState<Request>({});
    const setProbandRequest = useSetProbandRequest();
    const showAlert = CallAlertDialog();

    const fetchRequests = async (search: Query) => {
        const response = await postRequests(search);
        if (response.ok) {
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const data: Request[] = await response.json();
            setRequests(data);
            setTotalPage(totalPage);
        }
    }

    const handlePageChange = (newPageNumber: number) => {
        setSearch(prevPage =>({
            ...prevPage,
            page: newPageNumber
        }));
    };

    const handleRowClick = (request: Request) => {
        setSelectedRequest(request);
    };

    const handleConfirmClick = () => {
        if (selectedRequest) {
            setProbandRequest(selectedRequest);
            closeModal();
        } else {
            showAlert("Please select a row before confirming.");
        }
    };

    useEffect(() => {
        const updateSearch: Query = {
            ...search,
            filter_groups:[
                {
                    filters: defaultFilters
                },
                {
                    filters: [
                        ...(searchFilter ? [searchFilter] : [])
                    ]
                }
            ],
            sorts: [
                {
                    table: "sample",
                    column: "barcode"
                }
            ]
        }
        fetchRequests(updateSearch);
    }, [search, searchFilter]);

    const formatDate = (year: number | undefined, month: number | undefined, day: number | undefined) => {
        if (year !== undefined && month !== undefined && day !== undefined) {
            const date = new Date(year, month - 1, day);
            return format(date, "dd-MMM-yyyy");
        }
        return "-";
    };

    return (
        <div className={style.modalBackground}>
            <div className={style.modal}>
                <FontAwesomeIcon icon={faXmark} onClick={closeModal} className={style.modalCloseButton}/>
                <div className={style.modalTitle}>Proband Search</div>
                <div className={style.contentGroup}>
                    <div className={style.content} style={{width: '800px', height: '340px'}}>
                        <div className={managementStyle.filterContainerSearch}>
                            <SelectBox
                                width={"200px"}
                                value={selectedOption.name}
                                options={selectBoxOptions}
                                label={"status"}
                                onChange={(option) => {
                                    setSelectedOption(option);
                                }}
                            />
                            <div className={managementStyle.search}>
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
                                }}>
                                </InputBox>
                            </div>
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
                        <table className={tableStyle.table}>
                            <thead>
                            <tr>
                                <th style={{width: '150px'}}>Institution</th>
                                <th style={{width: '150px'}}>Service</th>
                                <th style={{width: '20px'}}>Registration ID</th>
                                <th style={{width: '150px'}}>Patient(s) Name</th>
                                <th style={{width: '100px'}}>MRN</th>
                                <th style={{width: '100px'}}>Patient(s) DOB</th>
                            </tr>
                            </thead>
                            <tbody>
                            {requests && requests.length > 0 ? ( requests?.map((request, index) => (
                                <tr key={index}
                                    onClick={() => handleRowClick(request)}
                                    className={`${tableStyle.selectRow} ${selectedRequest.sample?.barcode === request.sample?.barcode ? tableStyle.selected : ''}`}
                                >
                                    <td>{request.sample?.patient?.organization?.name}</td>
                                    <td>{request.service?.name}</td>
                                    <td>{request.sample?.barcode}</td>
                                    <td>{request.sample?.patient?.name}</td>
                                    <td>{request.sample?.patient?.serial}</td>
                                    <td>{formatDate(request.sample?.patient?.birth_year, request.sample?.patient?.birth_month, request.sample?.patient?.birth_day)}</td>
                                </tr>
                            ))
                            ) : (
                                <tr>
                                    <td colSpan={6} className={globalTableStyle.noData}>
                                        The searched data does not exist
                                    </td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>
                </div>
                <div className={style.buttonGroup}>
                    <BlueButton name={'Confirm'} onClick={handleConfirmClick}/>
                </div>
            </div>
        </div>
    )
}