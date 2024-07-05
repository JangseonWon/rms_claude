"use client"

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/management/user/_component/usersTable.module.css";
import {faAngleLeft, faAngleRight, faBuildingColumns, faMagnifyingGlass} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {Paging} from "@/model/Paging";
import {User} from "@/model/User";
import {getUsers} from "@/app/(afterLogin)/request/management/user/_api/getUsers";
import SwitchButton from "@/app/_component/SwitchButton";
import {fetchUserUpdate} from "@/app/(afterLogin)/_api/fetchUserUpdate";
import ServiceModal from "@/app/(afterLogin)/request/management/user/_component/ServiceModal";

interface UserWithSelected extends User {
    isSelected?: boolean;
}

export default function UsersTable() {
    const [userData, setUserData] = useState<UserWithSelected[]>([]);
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
            size: newSize
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
        console.log(search);
        console.log(searchValue);
        console.log(searchKey);
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

    const handleToggle = async (id: string, checked: boolean) => {
        const state = checked ? 'ACTIVE' : 'INACTIVE';
        const userUpdate = { id, state };
        await fetchUserUpdate(userUpdate);
        await fetchData(search);
    };

    const handleAlisSyncButtonClick = () => {
        alert('add click');
    }

    const handleInstitutionIconClick = (id: string) => {
        alert(`${id} add click`);
    }

    const handleServiceIconClick = (id: string) => {
        alert(`${id} add click`);
    }

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <>
            <section className={style.filterContainer}>
                <div className={style.filterContainerLeft}>
                    <button className={style.alisSync} onClick={handleAlisSyncButtonClick}>
                        Alis-Sync
                    </button>
                    <select className={style.selectSearchKey} onChange={handleSearchKeyChange}>
                        <option value="id">ID</option>
                        <option value="name">Name</option>
                        <option value="email">Email</option>
                        <option value="phone_number">Phone Number</option>
                        <option value="branch_name">Institution</option>
                        <option value="branch_serial">Serial</option>
                        <option value="role">Role</option>
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
                        <th>Email</th>
                        <th>Phone Number</th>
                        <th>Serial</th>
                        <th>Role</th>
                        <th>Institution</th>
                        <th>Service</th>
                        <th>State</th>
                    </tr>
                    </thead>
                    <tbody>
                    {userData && userData.length > 0 && userData.map((row, rowIndex) => (
                        <tr key={rowIndex}>
                            <td>{row.id}</td>
                            <td>{row.name}</td>
                            <td>{row.email}</td>
                            <td>{row.phone_number}</td>
                            <td>{row.branch_serial}</td>
                            <td>{row.role}</td>
                            <td>
                                <FontAwesomeIcon
                                    className={style.icon}
                                    icon={faMagnifyingGlass}
                                    onClick={()=> handleInstitutionIconClick(row.id)}
                                />
                            </td>
                            <td>
                                <ServiceModal id={row.id}/>
                            </td>
                            <td>
                                <SwitchButton
                                    id={row.id}
                                    checked={row.state === 'ACTIVE'}
                                    onToggle={handleToggle}
                                />
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
        </>
    );
}