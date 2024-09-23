import style from "./serviceSearchBox.module.css";
import React, {useEffect, useRef, useState} from "react";
import {faChevronDown} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {Filter} from "@/model/Filter";
import {Service} from "@/model/Service";
import {getUserWithServices} from "@/app/(afterLogin)/request/management/user/_api/getServicesByUserId";
import {useSession} from "next-auth/react";
import {useRouter} from "next/navigation";

export default function ServiceSearchBox() {
    return null
    /*const router = useRouter();
    const [selectedValue, setSelectedValue] = useState<string>('');
    const [isOpen, setIsOpen] = useState<boolean>(false)
    const [options, setOptions] = useState<SelectBoxOption[]>([]);
    const selectBoxRef = useRef<HTMLDivElement>(null);
    const { data: session } = useSession();

    const transformDataToOptions = (data: Service[]): SelectBoxOption[] => {
        if (!data) {
            return [];
        }
        return data.map(value => ({
            value: value.id,
            name: value.name
        }));
    };

    const toggleList = () => {
        setIsOpen(!isOpen)
    }

    const handleOptionClick = (option: SelectBoxOption) => {
        setSelectedValue(option.name!);
        router.push(`/request/services/${option.value}/single`);
        setIsOpen(!isOpen);
    };

    const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSelectedValue(event.target.value);
    };

    const fetchOptions = async () => {
        setOptions([]);
        const filter: Filter = { value: selectedValue };
        const response = await getUserWithServices(session?.user?.id!, filter);

        const data = await response.json();
        setOptions(transformDataToOptions(data));
    };

    const handleClickOutside = (event: MouseEvent) => {
        if (selectBoxRef.current && !selectBoxRef.current.contains(event.target as Node)) {
            setIsOpen(false);
        }
    };

    useEffect(() => {
        fetchOptions();
    }, [selectedValue]);

    useEffect(() => {
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    return (
        <div ref={selectBoxRef} className={style.container} onClick={toggleList}>
            <section className={style.selectSection}>
                <p className={style.label}>Search the service</p>
                <div className={`${style.btnSelect} ${isOpen ? style.open : ''}`}>
                    <input
                        className={style.selectInput}
                        type="text"
                        value={selectedValue}
                        onChange={handleSearchChange}
                    />
                    <FontAwesomeIcon icon={faChevronDown} className={style.icon}/>
                </div>
                <div className={`${style.searchList} ${isOpen ? style.open : ''}`}>
                    <ul className={style.listMember}>
                        {options.map((option) => (
                            <li key={option.name}>
                                <button onClick={() => handleOptionClick(option)}>
                                    {option.name}
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            </section>
        </div>
    )*/
}