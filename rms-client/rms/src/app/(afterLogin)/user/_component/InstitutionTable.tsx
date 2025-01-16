"use client"

import React, {useCallback, useEffect, useState} from "react";
import institutionStyle from "@/app/(afterLogin)/user/_component/institutionTable.module.css";
import globalTableStyle from "@/css/globalTable.module.css";
import type {Organization} from "@/model/Organization";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {postOrganizations} from "@/app/(afterLogin)/user/_api/postOrganizations";
import InputBox from "@/app/_component/InputBox";
import InstitutionAddModal from "@/app/(afterLogin)/user/_component/InstitutionAddModal";
import InstitutionEditModal from "@/app/(afterLogin)/user/_component/InstitutionEditModal";
import BlueButton from "@/app/_component/BlueButton";
import RectangleButton from "@/app/_component/RectangleButton";
import {Query} from "@/model/Query";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import SelectBox from "@/app/_component/SelectBox";

export default function InstitutionTable() {
    const [organizationData, setOrganizationData] = useState<Organization[]>([])
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] = useState<Query>({sort_by:"id", asc: false, size:10, page:1});
    const [pageRange, setPageRange] = useState<{ start: number, end: number }>({ start: 1, end: 10 });
    const [selectOption, setSelectOption] = useState<SelectBoxOption>({ table: "organization", column: "id", name: "Id" });
    const [selectInstitution, setSelectInstitution] = useState<Organization>();
    const [institutionEditModalOpen, setInstitutionEditModalOpen] = useState<boolean>(false);
    const [institutionAddModalOpen, setInstitutionAddModalOpen] = useState<boolean>(false);

    const selectBoxOptions: SelectBoxOption[] = [
        { table: "organization", column: "id", name: "Id" },
        { table: "organization", column: "name", name: "Name" },
        { table: "organization", column: "type", name: "Type" },
        { table: "organization", column: "nursing_number", name: "Nursing Number" },
        { table: "organization", column: "registration_number", name: "Registration Number" },
    ];

    const handlePageChange = (newPageNumber: number) => {
        setSearch(prevPage => ({
            ...prevPage,
            page: newPageNumber
        }));
        if (newPageNumber < pageRange.start || newPageNumber > pageRange.end) {
            const newStart = Math.floor((newPageNumber - 1) / 10) * 10 + 1;
            setPageRange({ start: newStart, end: newStart + 9 });
        }
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

    const openInstitutionAddModal = () => {
        setInstitutionAddModalOpen(true);
    }

    const closeInstitutionAddModal = () => {
        setInstitutionAddModalOpen(false);
        fetchData(search);
    }

    const openInstitutionEditModal = (organization: Organization) => {
        setSelectInstitution(organization);
        setInstitutionEditModalOpen(true);
    }

    const closeInstitutionEditModal = () => {
        setInstitutionEditModalOpen(false);
        fetchData(search);
    }

    const fetchData = useCallback(async (search: Query) => {
        const response = await postOrganizations(search);
        if (response.ok) {
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setOrganizationData(responseData as Organization[]);
            setTotalPage(totalPage);
        }
    }, []);

    useEffect(() => {
        setOrganizationData([]);
        fetchData(search)
    }, [search]);

    return (
        <div className={globalTableStyle.container}>
            <section className={institutionStyle.filterContainer}>
                <div className={institutionStyle.filterContainerLeft}>
                </div>
                <div className={institutionStyle.filterContainerRight}>
                    <BlueButton name={"Institution Add"} onClick={openInstitutionAddModal}/>
                    <SelectBox
                        value={selectOption.name}
                        options={selectBoxOptions}
                        label={" "}
                        onChange={(selectedOption) => {
                            setSelectOption(selectedOption);
                        }}
                    />
                    <InputBox
                        label={" "}
                        onChange={(value) => {
                            handleSearchChange(selectOption, value)
                        }}>
                    </InputBox>
                </div>
            </section>
            <table className={globalTableStyle.table}>
                <thead>
                <tr>
                    <th>Institution Id</th>
                    <th>Institute / Practice Name</th>
                    <th>Type</th>
                    <th>Registration Number</th>
                    <th>Nursing Number</th>
                    <th>Edit</th>
                </tr>
                </thead>
                <tbody>
                {organizationData && organizationData.length > 0 && organizationData.map((row) => (
                    <tr key={row.id + row.name! + row.type + row.user}>
                        <td>{row.id}</td>
                        <td>{row.name}</td>
                        <td>{row.type}</td>
                        <td>{row.registration_number}</td>
                        <td>{row.nursing_number}</td>
                        <td>
                            <RectangleButton name={'Edit'} onClick={() => openInstitutionEditModal(row)}/>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
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
            {institutionAddModalOpen && (
                <InstitutionAddModal
                    open={institutionAddModalOpen}
                    closeModal={closeInstitutionAddModal}
                />
            )}
            {institutionEditModalOpen && (
                <InstitutionEditModal
                    organization={selectInstitution!}
                    open={institutionEditModalOpen}
                    closeModal={closeInstitutionEditModal}
                />
            )}
        </div>
    );
}