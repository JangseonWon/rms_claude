"use client"

import style from "@/app/_component/blueButton.module.css"

type Props = {
    name: string
    onClick?: () => void
    disabled?: boolean
}
export default function BlueButton({name, onClick, disabled=false}: Props) {
    return (
        <button className={`${style.blueButton} ${disabled ? style.disable : ""}`} onClick={disabled ? undefined : onClick}>{name}</button>
    )
}