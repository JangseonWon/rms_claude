"use client"

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/management/service/_component/serviceTable.module.css";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {Paging} from "@/model/Paging";
import {getServiceCategory} from "@/app/(afterLogin)/request/management/service/_api/getServiceCategory";
import {ServiceManage} from "@/model/ServiceManage";
import ServiceEditModal from "@/app/(afterLogin)/request/management/service/_component/ServiceEditModal";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import RectangleButton from "@/app/_component/RectangleButton";
import BlueButton from "@/app/_component/BlueButton";

interface InstitutionWithSelected extends ServiceManage {
    isSelected?: boolean;
}

export default function ServiceTable() {
    const [serviceManageData, setServiceManageData] = useState<InstitutionWithSelected[]>([]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] =
        useState<Paging>({filters: [], sort_by:"name", asc: true, size:10, page:1});
    const [searchKey, setSearchKey] = useState<string>("service_id");
    const [searchValue, setSearchValue] = useState<string>("");
    const [serviceModalOpen, setServiceModalOpen] = useState<boolean>(false);
    const [selectedService, setSelectedService] = useState<ServiceManage>();
    const [selectOption, setSelectOption] = useState<string>('Service Id');

    const selectBoxOptions: SelectBoxOption[] = [
        { value: "service_id", name: "Service Id" },
        { value: "service_name", name: "Service Name" },
        { value: "category_id", name: "Category Id" },
        { value: "category_name", name: "Category Name" },
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
        const filterWithOperator = { ...newFilter, operator: "LIKE" };
        setSearch((prevSearch) => ({
            ...prevSearch,
            filters: [filterWithOperator],
            page:1
        }));
    };

    const handleSearchKeyChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const key = event.target.value;
        setSearchKey(key);
        handleSearchChange({key: key, value: searchValue});
    };

    const fetchData = async (search: Paging) => {
        try {
            const response = await getServiceCategory(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            const data = responseData.data;
            setServiceManageData(data as ServiceManage[]);
            setTotalPage(totalPage)
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setServiceManageData([]);
            setTotalPage(0);
        }
    }

    const handleAlisSyncClick = () => {
        alert('sync complete');
    }

    const handleServiceEditClick = (service: ServiceManage) => {
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
                        value={selectOption}
                        options={selectBoxOptions}
                        label={"status"}
                        onChange={(selectedOption) =>{
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
                        <th>Id</th>
                        <th>Name</th>
                        <th>Category Name</th>
                        <th>Edit</th>
                    </tr>
                    </thead>
                    <tbody>
                    {serviceManageData && serviceManageData.length > 0 && serviceManageData.map((row, rowIndex) => (
                        <tr key={rowIndex}>
                            <td>{row.service_id}</td>
                            <td>{row.service_name}</td>
                            <td>{row.category_name}</td>
                            <td>
                                <RectangleButton name={'Edit'} onClick={()=> handleServiceEditClick(row)}/>
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
            {serviceModalOpen && (
                <ServiceEditModal
                    service={selectedService}
                    open={serviceModalOpen}
                    closeModal={closeModal}
                    refreshData={refreshData}
                />
            )}
        </>
    );
}