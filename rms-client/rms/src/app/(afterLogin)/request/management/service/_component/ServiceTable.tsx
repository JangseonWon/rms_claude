"use client"

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/management/service/_component/serviceTable.module.css";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {Paging} from "@/model/Paging";
import {Organization} from "@/model/Organization";
import {getInstitution} from "@/app/(afterLogin)/request/management/service/_api/getInstitution";

interface InstitutionWithSelected extends Organization {
    isSelected?: boolean;
}

export default function ServiceTable() {
    const [institutionData, setInstitutionData] = useState<InstitutionWithSelected[]>([]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] =
        useState<Paging>({filters: [], sort_by:"name", asc: true, size:10, page:1});
    const [searchKey, setSearchKey] = useState<string>("id");
    const [searchValue, setSearchValue] = useState<string>("");

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
            filters: [filterWithOperator]
        }));
    };

    const handleSearchKeyChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const key = event.target.value;
        setSearchKey(key);
        handleSearchChange({key: key, value: searchValue});
    };

    const fetchData = async (search: Paging) => {
        try {
            const response = await getInstitution(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            const data = responseData.data;
            setInstitutionData(data as Organization[]);
            setTotalPage(totalPage)
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setInstitutionData([]);
            setTotalPage(0);
        }
    }

    const handleUserAddClick = () => {
        alert('sync complete');
    }

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <>
            <section className={style.filterContainer}>
                <div className={style.filterContainerLeft}>
                    <button className={style.alisSync} onClick={handleUserAddClick}>
                        Alis-Sync
                    </button>
                    <select className={style.selectSearchKey} onChange={handleSearchKeyChange}>
                        <option value="id">ID</option>
                        <option value="name">Name</option>
                        <option value="type">Type</option>
                        <option value="registration_number">Code</option>
                    </select>
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
                        <th>Type</th>
                        <th>Code</th>
                    </tr>
                    </thead>
                    <tbody>
                    {institutionData && institutionData.length > 0 && institutionData.map((row, rowIndex) => (
                        <tr key={rowIndex}>
                            <td>{row.id}</td>
                            <td>{row.name}</td>
                            <td>{row.type}</td>
                            <td>{row.registration_number}</td>
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
        </>
    );
}