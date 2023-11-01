package com.heisy.utils

import com.heisy.data.usecase.AuthUseCase
import com.heisy.domain.usecase.IAuthUseCase
import com.heisy.schema.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

object InjectionUtils {
    private var application: Application? = null
    private var mDatabase: Database? = null

    private var refreshLifeTime = 0L
    private var mRecoveryTime = 0L

    // services
    private var userService: UserService? = null
    private var tablesService: FreelsTablesService? = null
    private var profileService: ProfilesService? = null
    private var freelsService: FreelsService? = null
    private var linkService: LinksService? = null
    private var companyService: CompanyService? = null
    private var tokensService: TokensService? = null
    private var rowService: FreelsRowsService? = null
    private var commentService: CommentService? = null
    private var gradeService: GradeService? = null
    private var loadingService: LoadingService? = null

    // useCases
    private var authUseCase: IAuthUseCase? = null

    fun provideApplication(app: Application? = null): Application {
        if (application == null && app != null) application = app
        return application!!
    }


    fun provideDataBase(database: Database? = null): Database {
        if (mDatabase == null && database != null) mDatabase = database
        return mDatabase!!
    }


    fun provideUserService(database: Database = provideDataBase()): UserService {
        if (userService == null) userService = UserService(database)
        return userService!!
    }

    fun provideTablesService(database: Database = provideDataBase()): FreelsTablesService {
        if (tablesService == null) tablesService = FreelsTablesService(database)
        return tablesService!!
    }

    fun provideProfileService(database: Database = provideDataBase()): ProfilesService {
        if (profileService == null) profileService = ProfilesService(database)
        return profileService!!
    }

    fun provideRowService(database: Database = provideDataBase()): FreelsRowsService {
        if (rowService == null) rowService = FreelsRowsService(database)
        return rowService!!
    }

    fun provideFreelsService(database: Database = provideDataBase()): FreelsService {
        if (freelsService == null) freelsService = FreelsService(database)
        return freelsService!!
    }

    fun provideLinkService(database: Database = provideDataBase()): LinksService {
        if (linkService == null) linkService = LinksService(database)
        return linkService!!
    }

    fun provideCompanyService(database: Database = provideDataBase()): CompanyService {
        if (companyService == null) companyService = CompanyService(database)
        return companyService!!
    }

    fun provideTokensSerivce(
        database: Database = provideDataBase(),
        rLifeTime: Long = refreshLifeTime,
        recoveryTime: Long = mRecoveryTime
    ): TokensService {
        refreshLifeTime = rLifeTime
        mRecoveryTime = recoveryTime
        if (tokensService == null) tokensService = TokensService(database, refreshLifeTime, mRecoveryTime)
        return tokensService!!
    }

    fun provideCommentService(database: Database = provideDataBase()): CommentService {
        if (commentService == null) commentService = CommentService(database)
        return commentService!!
    }

    fun provideGradeService(database: Database = provideDataBase()): GradeService {
        if (gradeService == null) gradeService = GradeService(database)
        return gradeService!!
    }

    fun provideLoadingService(database: Database = provideDataBase()): LoadingService {
        if (loadingService == null) loadingService = LoadingService(database)
        return loadingService!!
    }


    // UseCases()
    fun provideAuthUseCase(): IAuthUseCase {
        if (authUseCase == null) authUseCase = AuthUseCase(
            userService = provideUserService(),
            freelsService = provideFreelsService(),
            linkService = provideLinkService(),
            profilesService = provideProfileService(),
            tokensService = provideTokensSerivce()
        )

        return authUseCase!!
    }

}