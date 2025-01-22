"use client"

import React, {useEffect, useState} from "react";
import globalTableStyle from "@/css/globalTable.module.css";
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
import {Filter} from "@/model/Filter";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

interface ServiceWithSelected extends Service {
    isSelected?: boolean;
}
const selectBoxOptions: SelectBoxOption[] = [
    { table: "service", column: "id", name: "Code" },
    { table: "service", column: "name_kr", name: "Name(KR)" },
    { table: "service", column: "name", name: "Name(EN)" },
    { table: "service", column: "group_name", name: "Group Name" },
    { table: "service", column: "type", name: "Type" },
    { table: "category", column: "name", name: "Category Name" },
];
const defaultSearch: Query = {
    sorts: [
        {
            table: "service",
            column: "id"
        }
    ],
    size:10,
    page:1
}

export default function ServiceManageTable() {
    const [isLoading, setIsLoading] = useState<boolean>(false)
    const [selectedOption, setSelectedOption] = useState<SelectBoxOption>(selectBoxOptions[0]);
    const [services, setServices] = useState<ServiceWithSelected[]>([]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] = useState<Query>(defaultSearch);
    const [searchFilter, setSearchFilter] = useState<Filter | undefined>(undefined);
    const [serviceModalOpen, setServiceModalOpen] = useState<boolean>(false);
    const [selectedService, setSelectedService] = useState<Service>();
    const showAlert = CallAlertDialog();

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

    const fetchData = async (search: Query) => {
        try {
            const response = await postServices(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setServices(responseData as Service[]);
            setTotalPage(totalPage);
        } catch(error) {
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
                showAlert("sync success!")
            }
        } catch(error) {
            showAlert(`fail: ${error}`)
        } finally {
            setIsLoading(false)
        }
    }

    useEffect(() => {
        const updatedSearch = {
            ...search,
            filter_groups: [
                {
                    filters: [
                        ...(searchFilter ? [searchFilter] : []),
                    ],
                },
            ],
        };
        fetchData(updatedSearch);
    }, [search,searchFilter]);

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
                        value={selectedOption.name}
                        options={selectBoxOptions}
                        label={"status"}
                        onChange={(selectedOption) => {
                            setSelectedOption(selectedOption);
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
                        }}/>
                    </div>
                </div>
            </section>
            <section className={managementStyle.tableContainer}>
                <table className={globalTableStyle.table}>
                    <thead>
                    <tr>
                        <th>Code</th>
                        <th>Name(KR)</th>
                        <th>Name(EN)</th>
                        <th>Category Name</th>
                        <th>Group Name</th>
                        <th>Type</th>
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
                            <td>{service.group_name}</td>
                            <td>{service.type}</td>
                            <td>
                                <RectangleButton name={'Edit'} onClick={() => handleServiceEditClick(service)}/>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </section>
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