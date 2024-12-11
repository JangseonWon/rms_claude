'use client';

import React, {useEffect, useState} from "react";
import style from '@/css/modal.module.css';
import tableStyle from '@/css/globalTable.module.css';
import globalTableStyle from '@/css/globalTable.module.css';
import {faAngleLeft, faAngleRight, faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import BlueButton from "@/app/_component/BlueButton";
import {Patient} from "@/model/Patient";
import managementStyle from "@/css/managementTable.module.css";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {Query} from "@/model/Query";
import {postPatients} from "@/app/(afterLogin)/request/services/[service]/single/_api/postPatients";
import {useSetProband} from "@/app/(afterLogin)/request/services/[service]/single/store/useProbandStore";
import {SampleType} from "@/model/SampleType";

type Props = {
    closeModal: () => void;
}

const selectBoxOptions: SelectBoxOption[] = [
    { table: "patient", column: "name", name: "Name" },
    { table: "patient", column: "user_id", name: "User Id" },
    { table: "patient", column: "serial", name: "Serial" },
    { table: "organization", column: "name", name: "Organization Name" }
];

export default function SearchProbandModal({ closeModal }: Props) {
    const [patients, setPatients] = useState<Patient[]>([]);
    const [selectOption, setSelectOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [search, setSearch] = useState<Query>({sort_by:"serial", asc: false, size:5, page:1});
    const [totalPage, setTotalPage] = useState<number>(0);
    const [selectedSerial, setSelectedSerial] = useState<string | null>(null);
    const setProband = useSetProband();

    const fetchPatient = async () => {
        const response = await postPatients(search);
        if (response.ok) {
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const data = await response.json();
            const patient = data as Patient[];
            setPatients(patient);
            setTotalPage(totalPage);
        }
    }

    const handleSearchChange = (option: SelectBoxOption, value: string) => {
        setSearch((prevSearch) => ({
            ...prevSearch,
            filter_groups:[
                {
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
    };

    const handlePageChange = (newPageNumber: number) => {
        setSearch(prevPage =>({
            ...prevPage,
            page: newPageNumber
        }));
    };

    const handleRowClick = (serial: string) => {
        setSelectedSerial(serial);
    };

    const handleConfirmClick = () => {
        if (selectedSerial) {
            setProband(selectedSerial);
            closeModal();
        } else {
            alert("Please select a row before confirming.");
        }
    };


    useEffect(() => {
        fetchPatient();
    }, [search]);

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
                                value={selectOption.name}
                                options={selectBoxOptions}
                                label={"status"}
                                onChange={(selectedOption) => {
                                    setSelectOption(selectedOption);
                                }}
                            />
                            <div className={managementStyle.search}>
                                <InputBox label={"search"} onChange={(value) => {
                                    handleSearchChange(selectOption, value)
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
                                <th style={{width:'150px'}}>Name</th>
                                <th style={{width:'150px'}}>Serial</th>
                                <th style={{width:'20px'}}>Sex</th>
                                <th style={{width:'150px'}}>Organization Name</th>
                                <th style={{width:'100px'}}>Birth</th>
                            </tr>
                            </thead>
                            <tbody>
                            {patients?.map((patient, index) => (
                                <tr key={index}
                                    onClick={() => handleRowClick(patient.serial!)}
                                    className={`${tableStyle.selectRow} ${selectedSerial === patient.serial ? tableStyle.selected : ''}`}
                                >
                                    <td>{patient.name}</td>
                                    <td>{patient.serial}</td>
                                    <td>{patient.sex}</td>
                                    <td>{patient.organization?.name}</td>
                                    <td>{`${patient.birth_year}-${patient.birth_month}-${patient.birth_day}`}</td>
                                </tr>
                            ))}
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