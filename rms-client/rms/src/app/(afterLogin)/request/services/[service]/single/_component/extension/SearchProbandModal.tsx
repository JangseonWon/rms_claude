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
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import {postRequests} from "@/app/(afterLogin)/request/services/[service]/single/_api/postRequests";
import {Filter} from "@/model/Filter";
import {getStringDateFromComponents} from "@/app/_component/DateUtil";

type Props = {
    request: Request
    closeModal: () => void;
    onConfirm: (proband: Request) => void
}

const selectBoxOptions: SelectBoxOption[] = [
    { table: "service", column: "name", name: "Service" },
    { table: "sample", column: "barcode", name: "Registration ID" },
    { table: "patient", column: "name", name: "Patient(s) Name" },
    { table: "patient", column: "serial", name: "MRN" }
];
export default function SearchProbandModal({ request, closeModal, onConfirm }: Props) {
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

    const [candidates, setCandidates] = useState<Request[]>([]);
    const [selected, setSelected] = useState<Request>({});
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [search, setSearch] = useState<Query>({size:5, page:1});
    const [searchFilter, setSearchFilter] = useState<Filter | undefined>(undefined);
    const [totalPage, setTotalPage] = useState<number>(0);
    const showAlert = CallAlertDialog();

    const fetchRequests = async (search: Query) => {
        const response = await postRequests(search);
        if (response.ok) {
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const data: Request[] = await response.json();
            setCandidates(data);
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
        setSelected(request);
    };

    const handleConfirm = () => {
        if (!selected) {
            showAlert("Please select a row before confirming.");
            return
        }
        onConfirm(selected)
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
                            {candidates && candidates.length > 0 ? ( candidates?.map((candidate, index) => (
                                <tr key={index}
                                    onClick={() => handleRowClick(candidate)}
                                    className={`${tableStyle.selectRow} ${selected.sample?.barcode === candidate.sample?.barcode ? tableStyle.selected : ''}`}
                                >
                                    <td>{candidate.sample?.patient?.organization?.name}</td>
                                    <td>{candidate.service?.name}</td>
                                    <td>{candidate.sample?.barcode}</td>
                                    <td>{candidate.sample?.patient?.name}</td>
                                    <td>{candidate.sample?.patient?.serial}</td>
                                    <td>{getStringDateFromComponents(candidate.sample?.patient?.birth_year, candidate.sample?.patient?.birth_month, candidate.sample?.patient?.birth_day)}</td>
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
                    <BlueButton name={'Confirm'} onClick={handleConfirm}/>
                </div>
            </div>
        </div>
    )
}