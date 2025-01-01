'use client';

import style from "@/css/globalTable.module.css";
import managementStyle from "@/css/managementTable.module.css";
import React, {useEffect, useState} from "react";
import RectangleButton from "@/app/_component/RectangleButton";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {Query} from "@/model/Query";
import InputBox from "@/app/_component/InputBox";
import BlueButton from "@/app/_component/BlueButton";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {SampleType} from "@/model/SampleType";
import {postSampleTypes} from "@/app/(afterLogin)/request/management/sample-type/_api/postSampleTypes";
import SampleTypeModal from "@/app/(afterLogin)/request/management/sample-type/_component/SampleTypeModal";
import LoadingFullScreen from "@/app/_component/LoadingFullScreen";
import {putAlisSampleTypes} from "@/app/(afterLogin)/request/management/sample-type/_api/putAlisSampleTypes";

const selectBoxOptions: SelectBoxOption[] = [
    { table: "sample_type", column: "id", name: "Code" },
    { table: "sample_type", column: "name_kr", name: "Name(KR)" },
    { table: "sample_type", column: "name", name: "Name(EN)" },
];
const defaultQuery: Query = {
    sorts: [
        {
            table: "sample_type",
            column: "id"
        }
    ],
    size:10,
    page:1
}
export default function SampleTypeTable() {
    const [isLoading, setIsLoading] = useState<boolean>(false)
    const [sampleTypes, setSampleTypes] = useState<SampleType[]>([]);
    const [selectSampleType, setSelectSampleType] = useState<SampleType>();
    const [editModalOpen, setEditModalOpen] = useState<boolean>(false);
    const [search, setSearch] = useState<Query>(defaultQuery);
    const [totalPage, setTotalPage] = useState<number>();
    const [selectOption, setSelectOption] = useState<SelectBoxOption>(selectBoxOptions[0]);


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
            const response = await postSampleTypes(search);
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setSampleTypes(responseData as SampleType[]);
            setTotalPage(totalPage);
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setSampleTypes([]);
            setTotalPage(0);
        }
    }

    const handleEditClick = (sampleType: SampleType) => {
        setSelectSampleType(sampleType)
        setEditModalOpen(true)
    }

    const closeModal = () => {
        setEditModalOpen(false);
    }

    const handleAlisSyncButtonClick = async(search: Query) => {
        try {
            setIsLoading(true)
            const response = await putAlisSampleTypes(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setSampleTypes(responseData as SampleType[]);
            setTotalPage(totalPage)
            if(response.ok){
                await fetchData(search)
                alert("sync success!")
            }
        } catch(error) {
            alert(`fail: ${error}`)
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
                        <th>Edit</th>
                    </tr>
                    </thead>
                    <tbody>
                    {sampleTypes && sampleTypes.length > 0 && sampleTypes.map((row, rowIndex) => (
                        <tr key={rowIndex}>
                            <td>{row.id}</td>
                            <td>{row.name_kr}</td>
                            <td>{row.name}</td>
                            <td>
                                <RectangleButton name={'Edit'} onClick={() => handleEditClick(row)}/>
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
            {editModalOpen && selectSampleType && (
                <SampleTypeModal
                    sampleTypeId={selectSampleType.id!!}
                    closeModal={closeModal}
                    refreshTable={() => fetchData(search)}
                />
            )}
        </div>
    );
}