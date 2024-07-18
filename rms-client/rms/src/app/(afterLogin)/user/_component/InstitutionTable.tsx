"use client"

import React, {useCallback, useEffect, useState} from "react";
import style from "@/app/(afterLogin)/user/_component/institutionTable.module.css";
import type {Organization} from "@/model/Organization";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import EditModal from "@/app/(afterLogin)/user/_component/EditModal";
import {fetchOrganization} from "@/app/(afterLogin)/user/_api/fetchOrganization";
import {useSession} from "next-auth/react";
import InputBox from "@/app/_component/InputBox";
import {Paging} from "@/model/Paging";

export default function InstitutionTable() {
    const [organizationData, setOrganizationData] = useState<Organization[]>([])
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] =
        useState<Paging>({filters: [], sort_by:"name", asc: true, size:5, page:1});
    const [searchKey, setSearchKey] = useState<string>("user_id");
    const [searchValue, setSearchValue] = useState<string>("");
    const { data: session, status } = useSession();

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
            page: 1
        }));
    };

    const handleSearchKeyChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const key = event.target.value;
        setSearchKey(key);
        handleSearchChange({key: key, value: searchValue});
    };

    const fetchData = useCallback(async (search: Paging) => {
        const response = await fetchOrganization(search);
        const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
        const responseData = await response.json();
        const data = responseData.data;

        setOrganizationData(data as Organization[]);
        setTotalPage(totalPage);
    }, [session?.user?.id]);

    useEffect(() => {
        setOrganizationData([]);
        fetchData(search)
    }, [search]);

    return (
        <div className={style.container}>
            <section className={style.searchContainer}>
                <select className={style.selectSearchKey} onChange={handleSearchKeyChange}>
                    <option value="user_id">User Name</option>
                    <option value="name">Name</option>
                    <option value="type">Type</option>
                    <option value="registration_number">Registration Number</option>
                    <option value="nursing_number">Nursing Number</option>
                </select>
                <InputBox label={"search"} onChange={(value) => {
                    setSearchValue(value);
                    handleSearchChange({key: searchKey, value: value})
                }}></InputBox>
            </section>
            <table className={style.table}>
                <thead>
                <tr>
                    <th>User Name</th>
                    <th>Institute / Practice Name</th>
                    <th>Type</th>
                    <th>Registration Number</th>
                    <th>Nursing Number</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                {organizationData && organizationData.length > 0 && organizationData.map((row) => (
                    <tr key={row.id + row.name! + row.type + row.user}>
                        <td>{row.user_id}</td>
                        <td>{row.name}</td>
                        <td>{row.type}</td>
                        <td>{row.registration_number}</td>
                        <td>{row.nursing_number}</td>
                        <td>
                            <EditModal id={row.id}/>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            <div className={style.pagination}>
                <span>items per page:</span>
                <div className={style.select}>
                    <select onChange={handlePageSizeChange}>
                        <option value="5">5</option>
                        <option value="10">10</option>
                        <option value="20">20</option>
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
        </div>
    );
}