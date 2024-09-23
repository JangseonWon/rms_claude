'use client';

import style from "./extensionTable.module.css";
import React, {useEffect, useState} from "react";
import {Extension} from "@/model/Extension";
import ExtensionModal from "@/app/(afterLogin)/request/management/additional-info/_component/ExtensionModal";
import RectangleButton from "@/app/_component/RectangleButton";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {Query} from "@/model/Query";
import InputBox from "@/app/_component/InputBox";
import {getExtensionsPage} from "@/app/(afterLogin)/request/management/additional-info/_api/getExtensionsPage";
import BlueButton from "@/app/_component/BlueButton";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";

export default function ExtensionTable() {
    const [extensionData, setExtensionData] = useState<Extension[]>([]);
    const [selectExtensionData, setSelectExtensionData] = useState<Extension>();
    const [extensionEditModalOpen, setExtensionEditModalOpen] = useState<boolean>(false);
    const [search, setSearch] = useState<Query>({sort_by:"id", asc: true, size:10, page:1});
    const [totalPage, setTotalPage] = useState<number>();
    const [selectOption, setSelectOption] = useState<SelectBoxOption>({ table: "extension", column: "id", name: "Code" });

    const selectBoxOptions: SelectBoxOption[] = [
        { table: "extension", column: "id", name: "Code" },
        { table: "extension", column: "name", name: "Name" },
        { table: "extension", column: "type", name: "Type" },
    ];

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
    };

    const fetchData = async (search: Query) => {
        try {
            const response = await getExtensionsPage(search);
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setExtensionData(responseData as Extension[]);
            setTotalPage(totalPage);
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setExtensionData([]);
            setTotalPage(0);
        }
    }

    const handleEditExtensionClick = (extension: Extension) => {
        setSelectExtensionData(extension)
        setExtensionEditModalOpen(true)
    }

    const closeModal = () => {
        setExtensionEditModalOpen(false);
    }

    const handleAlisSyncClick = () => {
        alert('sync complete');
    }

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <>
            <section className={style.searchContainer}>
                <div className={style.filterContainerLeft}>
                    <div className={style.alisSyncButton}>
                        <BlueButton name={"Alis-Sync"} onClick={handleAlisSyncClick}/>
                    </div>
                    <SelectBox
                        width={"7vw"}
                        value={selectOption.name}
                        options={selectBoxOptions}
                        label={"status"}
                        onChange={(selectedOption) => {
                            setSelectOption(selectedOption);
                        }}
                    />
                </div>
                <div className={style.filterContainerRight}>
                    <InputBox label={"search"} onChange={(value) => {
                        handleSearchChange(selectOption, value)
                    }}></InputBox>
                </div>
            </section>
            <section className={style.tableContainer}>
                <table className={style.table}>
                    <thead>
                    <tr>
                        <th>Code</th>
                        <th>Name</th>
                        <th>Type</th>
                        <th>Regex</th>
                        <th>Edit</th>
                    </tr>
                    </thead>
                    <tbody>
                    {extensionData && extensionData.length > 0 && extensionData.map((row, rowIndex) => (
                        <tr key={rowIndex}>
                            <td>{row.id}</td>
                            <td>{row.name}</td>
                            <td>{row.type}</td>
                            <td>{row.regex}</td>
                            <td>
                                <RectangleButton name={'Edit'} onClick={() => handleEditExtensionClick(row)}/>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
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
            </section>
            {extensionEditModalOpen && selectExtensionData && (
                <ExtensionModal
                    extensionData={selectExtensionData}
                    closeModal={closeModal}
                    refreshTable={() => fetchData(search)}
                />
            )}
        </>
    );
}