"use client"

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/management/user/_component/usersTable.module.css";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {Paging} from "@/model/Paging";
import {User} from "@/model/User";
import {getUsers} from "@/app/(afterLogin)/request/management/user/_api/getUsers";

interface UserWithSelected extends User {
    isSelected?: boolean;
}

export default function UsersTable() {
    const [userData, setUserData] = useState<UserWithSelected[]>([]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] =
        useState<Paging>({filters: [], sort_by:"name", asc: true, size:5, page:1});

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
            size: newSize
        }));
    };

    const handleSearchChange = (newFilter: { key: string; value: string }) => {
        setSearch((prevSearch) => {
            const updatedFilters = prevSearch.filters?.slice() || [];
            const existingFilterIndex = updatedFilters.findIndex((filter) => filter.key === newFilter.key)
            if (existingFilterIndex !== -1) updatedFilters[existingFilterIndex] = newFilter;
            else updatedFilters.push(newFilter);
            return { ...prevSearch, filters: updatedFilters }
        });
    };

    const fetchData = async (search: Paging) => {
        try {
            const response = await getUsers(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            const data = responseData.data;
            setUserData(data as User[]);
            setTotalPage(totalPage)
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setUserData([]);
            setTotalPage(0);
        }
    }

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <>
            <section className={style.filterContainer}>
                <button className={style.addButton}>
                    Add
                </button>
                <div className={style.filterContainerRight}>
                    <InputBox label={"search"} onChange={(value) => {
                        handleSearchChange({key: "search", value: value})
                    }}></InputBox>
                </div>
            </section>
            <section className={style.tableContainer}>
                <table className={style.table}>
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone Number</th>
                    <th>Institution</th>
                    <th>Serial</th>
                    <th>Role</th>
                    <th>State</th>
                </tr>
                </thead>
                    <tbody>
                    {userData && userData.length > 0 && userData.map((row, rowIndex) => (
                        <tr>
                            <td>{row.id}</td>
                            <td>{row.name}</td>
                            <td>{row.email}</td>
                            <td>{row.phone_number}</td>
                            <td>{row.branch_name}</td>
                            <td>{row.branch_serial}</td>
                            <td>{row.role}</td>
                            <td>{row.state}</td>
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
            </section>
        </>
    );
}