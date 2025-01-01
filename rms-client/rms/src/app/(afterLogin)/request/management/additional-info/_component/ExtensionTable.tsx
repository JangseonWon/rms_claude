'use client';

import style from "@/css/globalTable.module.css";
import managementStyle from "@/css/managementTable.module.css";
import React, {useEffect, useState} from "react";
import {Extension} from "@/model/Extension";
import ExtensionModal from "@/app/(afterLogin)/request/management/additional-info/_component/ExtensionModal";
import RectangleButton from "@/app/_component/RectangleButton";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {Query} from "@/model/Query";
import InputBox from "@/app/_component/InputBox";
import {postExtensions} from "@/app/(afterLogin)/request/management/additional-info/_api/postExtensions";
import BlueButton from "@/app/_component/BlueButton";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {putAlisExtensions} from "@/app/(afterLogin)/request/management/additional-info/_api/putAlisExtensions";
import LoadingFullScreen from "@/app/_component/LoadingFullScreen";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

const selectBoxOptions: SelectBoxOption[] = [
    { table: "extension", column: "id", name: "Code" },
    { table: "extension", column: "name_kr", name: "Name(KR)" },
    { table: "extension", column: "name", name: "Name(EN)" },
    { table: "extension", column: "type", name: "Type" },
];

const defaultQuery: Query = {
    sorts: [
        {
            table: "extension",
            column: "id"
        }
    ],
    size:10,
    page:1
}

export default function ExtensionTable() {
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [extensions, setExtensions] = useState<Extension[]>([]);
    const [selectExtension, setSelectExtension] = useState<Extension>();
    const [extensionEditModalOpen, setExtensionEditModalOpen] = useState<boolean>(false);
    const [search, setSearch] = useState<Query>(defaultQuery);
    const [totalPage, setTotalPage] = useState<number>();
    const [selectOption, setSelectOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const showAlert = CallAlertDialog();

    const handlePageChange = (newPageNumber: number) => {
        setSearch(prevPage =>({
            ...prevPage,
            page: newPageNumber
        }));
    };
    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newSize = parseInt(event.target.value);
        setSearch(prevSearch => ({
            ...prevSearch,
            size: newSize,
            page: 1
        }));
    };

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

    const fetchData = async (search: Query) => {
        try {
            const response = await postExtensions(search);
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setExtensions(responseData as Extension[]);
            setTotalPage(totalPage);
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setExtensions([]);
            setTotalPage(0);
        }
    }

    const handleEditExtensionClick = (extension: Extension) => {
        setSelectExtension(extension)
        setExtensionEditModalOpen(true)
    }

    const closeModal = () => {
        setExtensionEditModalOpen(false);
    }

    const handleAlisSyncButtonClick = async(search: Query) => {
        try {
            setIsLoading(true)
            const response = await putAlisExtensions(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setExtensions(responseData as Extension[]);
            setTotalPage(totalPage)
            if(response.ok){
                await fetchData(search)
                showAlert("sync success!")
            }
        } catch(error) {
            showAlert(`fail: ${error}`)
        } finally {
            setIsLoading(false)
        }
    }

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <div>
            {isLoading && <LoadingFullScreen/>}
            <section className={managementStyle.filterContainer}>
                <div className={managementStyle.filterContainerAlis}>
                    <div className={managementStyle.alisSyncButton}>
                        <BlueButton name={"Alis-Sync"} onClick={() => handleAlisSyncButtonClick(search)}/>
                    </div>
                </div>
                <div className={managementStyle.filterContainerSearch}>
                    <SelectBox
                        width={"140px"}
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
                        }}></InputBox>
                    </div>
                </div>
            </section>
            <section className={style.tableContainer}>
                <table className={style.table}>
                    <thead>
                    <tr>
                        <th>Code</th>
                        <th>Name(KR)</th>
                        <th>Name(EN)</th>
                        <th>Type</th>
                        <th>Edit</th>
                    </tr>
                    </thead>
                    <tbody>
                    {extensions && extensions.length > 0 && extensions.map((row, rowIndex) => (
                        <tr key={rowIndex}>
                            <td>{row.id}</td>
                            <td>{row.name_kr}</td>
                            <td>{row.name}</td>
                            <td>{row.type}</td>
                            <td>
                                <RectangleButton name={'Edit'} onClick={() => handleEditExtensionClick(row)}/>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </section>
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
                    disabled={search.page === 1}
                    onClick={() => handlePageChange((search.page ?? 1) - 1)}
                ><FontAwesomeIcon icon={faAngleLeft}/>
                </button>
                <button
                    disabled={search.page === totalPage}
                    onClick={() => handlePageChange((search.page ?? 1) + 1)}
                ><FontAwesomeIcon icon={faAngleRight}/>
                </button>
            </div>
            {extensionEditModalOpen && selectExtension && (
                <ExtensionModal
                    extensionId={selectExtension.id!!}
                    closeModal={closeModal}
                    refreshTable={() => fetchData(search)}
                />
            )}
        </div>
    );
}