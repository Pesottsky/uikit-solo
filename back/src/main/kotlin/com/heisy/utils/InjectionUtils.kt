package com.heisy.utils

import com.heisy.data.usecase.AuthUseCase
import com.heisy.domain.usecase.IAuthUseCase
import com.heisy.schema.*
import org.jetbrains.exposed.sql.Database

class InjectionUtils {
    private var mDatabase: Database? = null

    // services
    private var userService: UserService? = null
    private var tablesService: FreelsTablesService? = null
    private var profileService: ProfilesService? = null
    private var freelsService: FreelsService? = null
    private var linkService: LinksService? = null
    private var companyService: CompanyService? = null
    private var tokensService: TokensService? = null
    private var rowService: FreelsRowsService? = null

    // useCases
    private var authUseCase: IAuthUseCase? = null


    private fun provideDataBase(): Database {
        if (mDatabase == null) {
            mDatabase = Database.connect(
                url = "jdbc:sqlite:./main",
                driver = "org.sqlite.JDBC",
            )
        }
        return mDatabase!!
    }


    private fun provideUserService(database: Database = provideDataBase()) : UserService {
        if (userService == null) userService =  UserService(database)
        return userService!!
    }

    private fun provideTablesService(database: Database = provideDataBase()): FreelsTablesService {
        if (tablesService == null) tablesService =  FreelsTablesService(database)
        return tablesService!!
    }

    private fun provideProfileService(database: Database = provideDataBase()): ProfilesService {
        if (profileService == null) profileService =  ProfilesService(database)
        return profileService!!
    }

    private fun provideRowService(database: Database = provideDataBase()): FreelsRowsService {
        if (rowService == null) rowService = FreelsRowsService(database)
        return rowService!!
    }

    private fun provideFreelsService(database: Database = provideDataBase()): FreelsService {
        if (freelsService == null) freelsService = FreelsService(database)
        return freelsService!!
    }

    private fun provideLinkService(database: Database = provideDataBase()): LinksService {
        if (linkService == null) linkService = LinksService(database)
        return linkService!!
    }

    private fun provideCompanyService(database: Database = provideDataBase()): CompanyService {
        if (companyService == null) companyService = CompanyService(database)
        return companyService!!
    }

    private fun provideTokensSerivce(database: Database = provideDataBase()): TokensService {
        if (tokensService == null) tokensService = TokensService(database)
        return tokensService!!
    }


    // UseCases()
    fun provideAuthUseCase(): IAuthUseCase {
        if (authUseCase == null) authUseCase = AuthUseCase(
            userService = provideUserService(),
            freelsService = provideFreelsService(),
            linkService = provideLinkService(),
            rowService = provideRowService(),
            tokensService = provideTokensSerivce()
        )

        return authUseCase!!
    }

}