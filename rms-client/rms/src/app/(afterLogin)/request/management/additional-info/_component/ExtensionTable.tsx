'use client';

import style from "./extensionTable.module.css";
import React, {useEffect, useState} from "react";
import {Extensions} from "@/model/ServiceExtensionAndSampleType";
import ExtensionModal from "@/app/(afterLogin)/request/management/additional-info/_component/ExtensionModal";
import RectangleButton from "@/app/_component/RectangleButton";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {Paging} from "@/model/Paging";
import InputBox from "@/app/_component/InputBox";
import {getExtensionsPage} from "@/app/(afterLogin)/request/management/additional-info/_api/getExtensionsPage";
import BlueButton from "@/app/_component/BlueButton";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";

export default function ExtensionTable() {
    const [extensionData, setExtensionData] = useState<Extensions[]>([]);
    const [selectExtensionData, setSelectExtensionData] = useState<Extensions | undefined>();
    const [extensionEditModalOpen, setExtensionEditModalOpen] = useState<boolean>(false);
    const [extensionType, setExtensionType] = useState<string>('');
    const [searchKey, setSearchKey] = useState<string>("id");
    const [searchValue, setSearchValue] = useState<string>("");
    const [search, setSearch] =
        useState<Paging>({filters: [], sort_by:"name", asc: true, size:10, page:1});
    const [totalPage, setTotalPage] = useState<number>();
    const [selectOption, setSelectOption] = useState<string>('Code');

    const selectBoxOptions: SelectBoxOption[] = [
        { value: "id", name: "Code" },
        { value: "name", name: "Name" }
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

    const handleSearchChange = (newFilter: { key: string; value: string }) => {
        setSearch((prevSearch) => {
            const updatedFilters = prevSearch.filters?.slice() || [];
            const existingFilterIndex = updatedFilters.findIndex((filter) => filter.key === newFilter.key);
            if (existingFilterIndex !== -1) {
                updatedFilters[existingFilterIndex] = newFilter;
            } else {
                updatedFilters.push(newFilter);
            }
            return { ...prevSearch, filters: updatedFilters, page:1 }
        });
    };

    const handleSearchKeyChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const key = event.target.value;
        setSearchKey(key);
        handleSearchChange({key: key, value: searchValue});
    };

    const fetchData = async (search: Paging) => {
        try {
            const response = await getExtensionsPage(search);
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            const data = responseData.data;
            setExtensionData(data as Extensions[]);
            setTotalPage(totalPage);
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setExtensionData([]);
            setTotalPage(0);
        }
    }

    const handleEditExtensionClick = (extension: Extensions) => {
        setExtensionEditModalOpen(true);
        setSelectExtensionData(extension);
        const type = mapRegexToType(extension.regex);
        setExtensionType(type);
    }

    const closeModal = () => {
        setExtensionEditModalOpen(false);
    }

    const mapRegexToType = (regex: string): string => {
        if (regex === "\\b(?:true|false)\\b") {
            return "Boolean";
        } else if (regex === "-?\\d+(\\.\\d+)?") {
            return "Number";
        } else if (regex === "-?\\d+") {
            return "Number";
        } else if (regex.includes("|")) {
            return "List";
        } else {
            return "String";
        }
    }

    const mapRegexToValue = (regex: string): string => {
        if (regex.includes("|")) {
            return regex
                .replace(/\\b|\b/g, '')
                .replace(/\\|\(|\)|\?:/g, '')
                .split('|')
                .filter(value => value !== 'true' && value !== 'false')
                .join(',');
        }
        return "";
    };

    const handleAlisSyncClick = () => {
        alert('sync complete');
    }

    useEffect(() => {
        fetchData(search);
    }, []);

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
                        value={selectOption}
                        options={selectBoxOptions}
                        label={"status"}
                        onChange={(selectedOption) => {
                            setSelectOption(selectedOption.value);
                            handleSearchKeyChange({target: {value: selectedOption.value}} as React.ChangeEvent<HTMLSelectElement>);
                        }}
                    />
                </div>
                <div className={style.filterContainerRight}>
                    <InputBox label={"search"} onChange={(value) => {
                        setSearchValue(value);
                        handleSearchChange({key: searchKey, value: value})
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
                        <th>List Value</th>
                        <th>Edit</th>
                    </tr>
                    </thead>
                    <tbody>
                    {extensionData && extensionData.length > 0 && extensionData.map((row, rowIndex) => (
                        <tr key={rowIndex}>
                            <td>{row.id}</td>
                            <td>{row.name}</td>
                            <td>{mapRegexToType(row.regex)}</td>
                            <td>{mapRegexToValue(row.regex)}</td>
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
                        onClick={() => handlePageChange(search.page - 1)}
                    ><FontAwesomeIcon icon={faAngleLeft}/>
                    </button>
                    <button
                        disabled={search.page === totalPage}
                        onClick={() => handlePageChange(search.page + 1)}
                    ><FontAwesomeIcon icon={faAngleRight}/>
                    </button>
                </div>
            </section>
            {extensionEditModalOpen && selectExtensionData && (
                <ExtensionModal
                    getExtension={selectExtensionData}
                    type={extensionType}
                    open={extensionEditModalOpen}
                    closeModal={closeModal}
                    refreshTable={() => fetchData(search)}
                />
            )}
        </>
    );
}