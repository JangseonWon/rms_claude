"use client"

import React, {useEffect, useState} from "react";
import style from "@/css/globalTable.module.css";
import managementStyle from "@/css/managementTable.module.css";
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
import {putAlisServices} from "@/app/(afterLogin)/request/management/service/_api/putAlisServices";
import LoadingFullScreen from "@/app/_component/LoadingFullScreen";

interface ServiceWithSelected extends Service {
    isSelected?: boolean;
}
const selectBoxOptions: SelectBoxOption[] = [
    { table: "service", column: "id", name: "Code" },
    { table: "service", column: "name_kr", name: "Name(KR)" },
    { table: "service", column: "name", name: "Name(EN)" },
    { table: "category", column: "name", name: "Category Name" },
];

export default function ServiceManageTable() {
    const [isLoading, setIsLoading] = useState<boolean>(false)
    const [services, setServices] = useState<ServiceWithSelected[]>([]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] = useState<Query>({sort_by:"id", asc: true, size:10, page:1});
    const [selectOption, setSelectOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [serviceModalOpen, setServiceModalOpen] = useState<boolean>(false);
    const [selectedService, setSelectedService] = useState<Service>();

    const handleServiceEditClick = (service: Service) => {
        setSelectedService(service);
        setServiceModalOpen(true);
        document.body.style.overflow = 'hidden';
    }
    const closeModal = () => {
        setSelectedService(undefined);
        setServiceModalOpen(false);
        document.body.style.overflow = 'auto';
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
            setServices(responseData as Service[]);
            setTotalPage(totalPage);
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setServices([]);
            setTotalPage(0);
        }
    }

    const handleAlisSyncButtonClick = async(search: Query) => {
        try {
            setIsLoading(true)
            const response = await putAlisServices(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setServices(responseData as Service[]);
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
                        width={"7vw"}
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
            </section>
            <section className={style.tableContainer}>
                <table className={style.table}>
                    <thead>
                    <tr>
                        <th>Code</th>
                        <th>Name(KR)</th>
                        <th>Name(EN)</th>
                        <th>Category Name</th>
                        <th>Edit</th>
                    </tr>
                    </thead>
                    <tbody>
                    {services && services.length > 0 && services.map((service, rowIndex) => (
                        <tr key={rowIndex}>
                            <td>{service.id}</td>
                            <td>{service.name_kr}</td>
                            <td>{service.name}</td>
                            <td>{service.category?.name}</td>
                            <td>
                                <RectangleButton name={'Edit'} onClick={() => handleServiceEditClick(service)}/>
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
            {serviceModalOpen && (
                <ServiceEditModal
                    serviceId={selectedService?.id!}
                    closeModal={closeModal}
                    refreshData={refreshData}
                />
            )}
        </div>
    );
}