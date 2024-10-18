"use client"

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/management/service/_component/serviceManageTable.module.css";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {Query} from "@/model/Query";
import {postServices} from "@/app/(afterLogin)/request/management/service/_api/postServices";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import BlueButton from "@/app/_component/BlueButton";
import RectangleButton from "@/app/_component/RectangleButton";
import ServiceEditModal from "@/app/(afterLogin)/request/management/service/_component/ServiceEditModal";
import {Service} from "@/model/Service";

interface InstitutionWithSelected extends Service {
    isSelected?: boolean;
}

export default function ServiceManageTable() {
    const [serviceManageData, setServiceManageData] = useState<InstitutionWithSelected[]>([]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] = useState<Query>({sort_by:"id", asc: true, size:10, page:1});
    const [selectOption, setSelectOption] = useState<SelectBoxOption>({ table: "service", column: "id", name: "Service Id" });
    const [serviceModalOpen, setServiceModalOpen] = useState<boolean>(false);
    const [selectedService, setSelectedService] = useState<Service>();

    const selectBoxOptions: SelectBoxOption[] = [
        { table: "service", column: "id", name: "Service Id" },
        { table: "service", column: "name", name: "Service Name" },
        { table: "category", column: "name", name: "Category Name" },
    ];

    const handleServiceEditClick = (service: Service) => {
        setSelectedService(service);
        setServiceModalOpen(true);
    }
    const closeModal = () => {
        setSelectedService(undefined);
        setServiceModalOpen(false);
    }
    const refreshData = () => {
        fetchData(search);
    }

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
            const response = await postServices(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setServiceManageData(responseData as Service[]);
            setTotalPage(totalPage);
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setServiceManageData([]);
            setTotalPage(0);
        }
    }

    const handleAlisSyncClick = () => {
        alert('sync complete');
    }

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <>
            <section className={style.filterContainer}>
                <div className={style.filterContainerLeft}>
                    <div className={style.alisSyncButton}>
                        <BlueButton name={"Alis-Sync"} onClick={handleAlisSyncClick}/>
                    </div>
                    <SelectBox
                        width={"7vw"}
                        value={selectOption.name}
                        options={selectBoxOptions}
                        label={"status"}
                        onChange={(selectedOption) =>{
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
                        <th>Id</th>
                        <th>Name</th>
                        <th>Category Name</th>
                        <th>Edit</th>
                    </tr>
                    </thead>
                    <tbody>
                    {serviceManageData && serviceManageData.length > 0 && serviceManageData.map((row, rowIndex) => (
                        <tr key={rowIndex}>
                            <td>{row.id}</td>
                            <td>{row.name}</td>
                            <td>{row.category?.name}</td>
                            <td>
                                <RectangleButton name={'Edit'} onClick={() => handleServiceEditClick(row)}/>
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
            {serviceModalOpen && (
                <ServiceEditModal
                    serviceId={selectedService?.id!}
                    closeModal={closeModal}
                    refreshData={refreshData}
                />
            )}
        </>
    );
}